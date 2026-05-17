package ke.co.bungesummary.api.controller;

import java.util.UUID;
import ke.co.bungesummary.api.dto.PagedResponse;
import ke.co.bungesummary.api.dto.SittingDetailResponse;
import ke.co.bungesummary.api.dto.SittingSummaryResponse;
import ke.co.bungesummary.api.service.SittingService;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/sittings")
@Profile("api")
public class SittingController {

    private final SittingService sittingService;

    public SittingController(SittingService sittingService) {
        this.sittingService = sittingService;
    }

    @GetMapping
    public ResponseEntity<PagedResponse<SittingSummaryResponse>> listSittings(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(sittingService.list(year, month, page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SittingDetailResponse> getSitting(@PathVariable UUID id) {
        return ResponseEntity.ok(sittingService.getById(id));
    }
}
