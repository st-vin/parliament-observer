package ke.co.bungesummary.ingestion.agent;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ContributionExtraction(
        @JsonProperty("member_name") String memberName,
        String summary,
        String stance,
        @JsonProperty("verbatim_excerpt") String verbatimExcerpt,
        @JsonProperty("stance_confidence") double stanceConfidence) {}
