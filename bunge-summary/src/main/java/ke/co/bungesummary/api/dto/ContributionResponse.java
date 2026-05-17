package ke.co.bungesummary.api.dto;

import java.util.UUID;

public record ContributionResponse(
        UUID id,
        UUID memberId,
        String memberName,
        String summary,
        String stance,
        String verbatimExcerpt,
        Integer excerptPage,
        Integer sequenceInProceeding) {}
