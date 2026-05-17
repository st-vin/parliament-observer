package ke.co.bungesummary.api.controller;

import java.util.List;
import ke.co.bungesummary.api.dto.PagedResponse;
import ke.co.bungesummary.api.dto.ProceedingSummaryResponse;
import ke.co.bungesummary.api.dto.TopicResponse;
import ke.co.bungesummary.api.service.TopicService;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/topics")
@Profile("api")
public class TopicController {

    private final TopicService topicService;

    public TopicController(TopicService topicService) {
        this.topicService = topicService;
    }

    @GetMapping
    public ResponseEntity<List<TopicResponse>> listTopics() {
        return ResponseEntity.ok(topicService.listAll());
    }

    @GetMapping("/{slug}/proceedings")
    public ResponseEntity<PagedResponse<ProceedingSummaryResponse>> proceedingsByTopic(
            @PathVariable String slug,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(topicService.proceedingsBySlug(slug, page, size));
    }
}
