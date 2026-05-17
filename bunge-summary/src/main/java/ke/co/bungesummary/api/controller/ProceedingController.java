package ke.co.bungesummary.api.controller;

import java.time.LocalDate;
import java.util.UUID;
import ke.co.bungesummary.api.dto.PagedResponse;
import ke.co.bungesummary.api.dto.ProceedingDetailResponse;
import ke.co.bungesummary.api.dto.ProceedingSummaryResponse;
import ke.co.bungesummary.api.service.ProceedingService;
import org.springframework.context.annotation.Profile;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/proceedings")
@Profile("api")
public class ProceedingController {

    private final ProceedingService proceedingService;

    public ProceedingController(ProceedingService proceedingService) {
        this.proceedingService = proceedingService;
    }

    @GetMapping
    public ResponseEntity<PagedResponse<ProceedingSummaryResponse>> listProceedings(
            @RequestParam(required = false) UUID sittingId,
            @RequestParam(required = false) String topic,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                    LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                    LocalDate dateTo,
            @RequestParam(required = false) UUID memberId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(
                proceedingService.list(sittingId, topic, dateFrom, dateTo, memberId, page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProceedingDetailResponse> getProceeding(@PathVariable UUID id) {
        return ResponseEntity.ok(proceedingService.getById(id));
    }
}
