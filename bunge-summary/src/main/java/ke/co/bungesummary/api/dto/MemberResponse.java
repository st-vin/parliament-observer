package ke.co.bungesummary.api.dto;

import java.util.UUID;

public record MemberResponse(
        UUID id,
        String fullName,
        String displayName,
        String constituency,
        String county,
        String party,
        String chamber,
        boolean active) {}
