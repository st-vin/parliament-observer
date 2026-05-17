package ke.co.bungesummary.api.dto;

import java.time.LocalDateTime;

public record TrackedTopicResponse(String name, String slug, LocalDateTime subscribedAt) {}
