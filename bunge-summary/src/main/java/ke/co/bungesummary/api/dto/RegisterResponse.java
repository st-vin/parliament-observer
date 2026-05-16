package ke.co.bungesummary.api.dto;

import java.util.UUID;

public record RegisterResponse(UUID id, String email, boolean emailVerified) {}
