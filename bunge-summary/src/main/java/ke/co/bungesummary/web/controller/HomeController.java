package ke.co.bungesummary.web.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import ke.co.bungesummary.api.dto.ProceedingSummaryResponse;
import ke.co.bungesummary.api.dto.TrackedTopicResponse;
import ke.co.bungesummary.api.mapper.ApiMapper;
import ke.co.bungesummary.api.service.ProceedingService;
import ke.co.bungesummary.api.service.TrackingService;
import ke.co.bungesummary.domain.entity.Proceeding;
import ke.co.bungesummary.domain.entity.Sitting;
import ke.co.bungesummary.domain.repository.ProceedingRepository;
import ke.co.bungesummary.domain.repository.SittingRepository;
import ke.co.bungesummary.security.UserPrincipal;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Profile("api")
public class HomeController {

    private final ProceedingService proceedingService;
    private final SittingRepository sittingRepository;
    private final ProceedingRepository proceedingRepository;
    private final TrackingService trackingService;

    public HomeController(
            ProceedingService proceedingService,
            SittingRepository sittingRepository,
            ProceedingRepository proceedingRepository,
            TrackingService trackingService) {
        this.proceedingService = proceedingService;
        this.sittingRepository = sittingRepository;
        this.proceedingRepository = proceedingRepository;
        this.trackingService = trackingService;
    }

    @GetMapping("/")
    public String home(@AuthenticationPrincipal UserPrincipal principal, Model model) {
        long sittingCount = sittingRepository.count();
        long proceedingCount = proceedingRepository.count();
        LocalDate lastUpdated =
                sittingRepository
                        .findTopByOrderByDateDesc()
                        .map(Sitting::getDate)
                        .orElse(null);

        List<ProceedingSummaryResponse> recent =
                proceedingService.list(null, null, null, null, null, 0, 5).content();

        model.addAttribute("title", "Home");
        model.addAttribute("sittingCount", sittingCount);
        model.addAttribute("proceedingCount", proceedingCount);
        model.addAttribute("lastUpdated", lastUpdated);
        model.addAttribute("recentProceedings", recent);

        if (principal != null) {
            List<String> slugs =
                    trackingService.listSubscriptions(principal.getId()).stream()
                            .map(TrackedTopicResponse::slug)
                            .toList();
            if (!slugs.isEmpty()) {
                List<Proceeding> feed =
                        proceedingRepository.findByTopicSlugIn(slugs, PageRequest.of(0, 10));
                Map<UUID, ProceedingSummaryResponse> deduped = new LinkedHashMap<>();
                for (Proceeding p : feed) {
                    deduped.putIfAbsent(p.getId(), ApiMapper.toSummary(p));
                }
                model.addAttribute("trackedFeed", new ArrayList<>(deduped.values()));
            }
        }

        return "home";
    }
}
