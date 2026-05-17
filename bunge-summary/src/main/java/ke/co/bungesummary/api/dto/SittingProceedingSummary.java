package ke.co.bungesummary.api.dto;

import java.util.UUID;

public record SittingProceedingSummary(
        UUID id, String title, String proceedingType, String outcome, Integer sequenceInSitting) {}
