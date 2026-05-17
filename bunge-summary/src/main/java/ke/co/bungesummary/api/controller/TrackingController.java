package ke.co.bungesummary.api.controller;

import java.util.List;
import ke.co.bungesummary.api.dto.TrackTopicsRequest;
import ke.co.bungesummary.api.dto.TrackedTopicResponse;
import ke.co.bungesummary.api.service.TrackingService;
import ke.co.bungesummary.security.UserPrincipal;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/tracking/topics")
@Profile("api")
public class TrackingController {

    private final TrackingService trackingService;

    public TrackingController(TrackingService trackingService) {
        this.trackingService = trackingService;
    }

    @GetMapping
    public ResponseEntity<List<TrackedTopicResponse>> listSubscriptions(
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(trackingService.listSubscriptions(principal.getId()));
    }

    @PostMapping
    public ResponseEntity<List<TrackedTopicResponse>> subscribe(
            @AuthenticationPrincipal UserPrincipal principal, @RequestBody TrackTopicsRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(trackingService.subscribe(principal.getId(), request));
    }

    @DeleteMapping("/{topicSlug}")
    public ResponseEntity<Void> unsubscribe(
            @AuthenticationPrincipal UserPrincipal principal, @PathVariable String topicSlug) {
        trackingService.unsubscribe(principal.getId(), topicSlug);
        return ResponseEntity.noContent().build();
    }
}
