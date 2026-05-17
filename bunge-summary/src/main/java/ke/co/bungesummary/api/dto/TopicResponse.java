package ke.co.bungesummary.api.dto;

import java.util.UUID;

public record TopicResponse(UUID id, String name, String slug, String description, long proceedingCount) {}
