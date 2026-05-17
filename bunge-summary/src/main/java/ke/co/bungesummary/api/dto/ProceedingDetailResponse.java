package ke.co.bungesummary.api.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record ProceedingDetailResponse(
        UUID id,
        UUID sittingId,
        LocalDate sittingDate,
        String title,
        String proceedingType,
        String stage,
        String outcome,
        String plainSummary,
        String rawText,
        Integer sourcePageStart,
        Integer sourcePageEnd,
        List<TopicTagResponse> topics,
        List<ContributionResponse> contributions) {}
