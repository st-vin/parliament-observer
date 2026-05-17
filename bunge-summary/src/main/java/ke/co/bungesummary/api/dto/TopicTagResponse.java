package ke.co.bungesummary.api.dto;

import java.math.BigDecimal;

public record TopicTagResponse(String name, String slug, BigDecimal confidenceScore) {}
