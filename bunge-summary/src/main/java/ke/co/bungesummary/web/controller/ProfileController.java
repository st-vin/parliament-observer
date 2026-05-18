package ke.co.bungesummary.web.controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import ke.co.bungesummary.api.dto.ProceedingSummaryResponse;
import ke.co.bungesummary.api.dto.TopicResponse;
import ke.co.bungesummary.api.dto.TrackTopicsRequest;
import ke.co.bungesummary.api.dto.TrackedTopicResponse;
import ke.co.bungesummary.api.mapper.ApiMapper;
import ke.co.bungesummary.api.service.TopicService;
import ke.co.bungesummary.api.service.TrackingService;
import ke.co.bungesummary.domain.entity.Proceeding;
import ke.co.bungesummary.domain.entity.User;
import ke.co.bungesummary.domain.repository.ProceedingRepository;
import ke.co.bungesummary.domain.repository.UserRepository;
import ke.co.bungesummary.security.UserPrincipal;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@Profile("api")
public class ProfileController {

    private final TrackingService trackingService;
    private final TopicService topicService;
    private final UserRepository userRepository;
    private final ProceedingRepository proceedingRepository;

    public ProfileController(
            TrackingService trackingService,
            TopicService topicService,
            UserRepository userRepository,
            ProceedingRepository proceedingRepository) {
        this.trackingService = trackingService;
        this.topicService = topicService;
        this.userRepository = userRepository;
        this.proceedingRepository = proceedingRepository;
    }

    @GetMapping("/profile")
    public String profile(@AuthenticationPrincipal UserPrincipal principal, Model model) {
        User user =
                userRepository
                        .findById(principal.getId())
                        .orElseThrow(
                                () ->
                                        new ResponseStatusException(
                                                HttpStatus.NOT_FOUND, "User not found"));

        List<TrackedTopicResponse> subscriptions =
                trackingService.listSubscriptions(principal.getId());
        Set<String> trackedSlugs =
                subscriptions.stream().map(TrackedTopicResponse::slug).collect(Collectors.toSet());

        List<TopicResponse> allTopics = topicService.listAll();
        List<TopicResponse> untracked =
                allTopics.stream().filter(t -> !trackedSlugs.contains(t.slug())).toList();

        model.addAttribute("title", "My Profile");
        model.addAttribute("user", user);
        model.addAttribute("subscriptions", subscriptions);
        model.addAttribute("untrackedTopics", untracked);

        if (!trackedSlugs.isEmpty()) {
            List<Proceeding> feed =
                    proceedingRepository.findByTopicSlugIn(
                            new ArrayList<>(trackedSlugs), PageRequest.of(0, 15));
            Map<UUID, ProceedingSummaryResponse> deduped = new LinkedHashMap<>();
            for (Proceeding p : feed) {
                deduped.putIfAbsent(p.getId(), ApiMapper.toSummary(p));
            }
            model.addAttribute("trackedFeed", new ArrayList<>(deduped.values()));
        }

        return "profile";
    }

    @PostMapping("/profile/topics/{slug}/track")
    public String track(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable String slug,
            @RequestParam(defaultValue = "/profile") String returnTo,
            RedirectAttributes redirectAttributes) {
        trackingService.subscribe(principal.getId(), new TrackTopicsRequest(null, slug));
        redirectAttributes.addFlashAttribute("message", "Now tracking topic");
        return "redirect:" + returnTo;
    }

    @PostMapping("/profile/topics/{slug}/untrack")
    public String untrack(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable String slug,
            @RequestParam(defaultValue = "/profile") String returnTo,
            RedirectAttributes redirectAttributes) {
        trackingService.unsubscribe(principal.getId(), slug);
        redirectAttributes.addFlashAttribute("message", "Stopped tracking topic");
        return "redirect:" + returnTo;
    }
}
