package ke.co.bungesummary.api.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record SittingDetailResponse(
        UUID id,
        LocalDate date,
        String chamber,
        String ingestionStatus,
        List<SittingProceedingSummary> proceedings) {}
