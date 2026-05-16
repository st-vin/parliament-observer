package ke.co.bungesummary.api.dto;

import java.util.UUID;

public record LoginResponse(String token, UUID userId, String email) {}
