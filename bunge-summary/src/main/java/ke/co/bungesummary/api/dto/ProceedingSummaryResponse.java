package ke.co.bungesummary.api.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record ProceedingSummaryResponse(
        UUID id,
        String title,
        LocalDate sittingDate,
        String proceedingType,
        String outcome,
        String plainSummary,
        List<TopicTagResponse> topics) {}
