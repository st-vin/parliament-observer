package ke.co.bungesummary.web.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import ke.co.bungesummary.api.dto.PagedResponse;
import ke.co.bungesummary.api.dto.ProceedingSummaryResponse;
import ke.co.bungesummary.api.dto.TopicResponse;
import ke.co.bungesummary.api.dto.TrackedTopicResponse;
import ke.co.bungesummary.api.service.TopicService;
import ke.co.bungesummary.api.service.TrackingService;
import ke.co.bungesummary.security.UserPrincipal;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@Profile("api")
public class BrowseTopicController {

    private final TopicService topicService;
    private final TrackingService trackingService;

    public BrowseTopicController(TopicService topicService, TrackingService trackingService) {
        this.topicService = topicService;
        this.trackingService = trackingService;
    }

    @GetMapping("/browse/topic")
    public String topicGrid(Model model) {
        model.addAttribute("title", "Browse by Topic");
        model.addAttribute("topics", topicService.listAll());
        return "browse-topic";
    }

    @GetMapping("/browse/topic/{slug}")
    public String topicProceedings(
            @PathVariable String slug,
            @RequestParam(defaultValue = "0") int page,
            @AuthenticationPrincipal UserPrincipal principal,
            Model model) {
        PagedResponse<ProceedingSummaryResponse> proceedings =
                topicService.proceedingsBySlug(slug, page, 20);
        List<TopicResponse> allTopics = topicService.listAll();
        TopicResponse topic =
                allTopics.stream()
                        .filter(t -> t.slug().equals(slug))
                        .findFirst()
                        .orElseThrow();

        Set<String> trackedSlugs = new HashSet<>();
        if (principal != null) {
            trackedSlugs =
                    trackingService.listSubscriptions(principal.getId()).stream()
                            .map(TrackedTopicResponse::slug)
                            .collect(Collectors.toSet());
        }

        model.addAttribute("title", topic.name());
        model.addAttribute("topic", topic);
        model.addAttribute("proceedings", proceedings.content());
        model.addAttribute("page", proceedings.page());
        model.addAttribute("trackedSlugs", trackedSlugs);
        model.addAttribute("isTracked", trackedSlugs.contains(slug));
        return "browse-topic-detail";
    }
}
