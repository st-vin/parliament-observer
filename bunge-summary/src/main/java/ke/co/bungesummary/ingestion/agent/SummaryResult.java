package ke.co.bungesummary.ingestion.agent;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SummaryResult(@JsonProperty("plain_summary") String plainSummary) {}
