package ke.co.bungesummary.ingestion.agent;

public record ProceedingMetadata(
        String title,
        String proceedingType,
        String stage,
        String outcome,
        double confidenceScore) {}
