package ke.co.bungesummary.api.dto;

import java.time.LocalDate;
import java.util.UUID;

public record SittingSummaryResponse(
        UUID id,
        LocalDate date,
        String chamber,
        long proceedingCount,
        String ingestionStatus) {}
