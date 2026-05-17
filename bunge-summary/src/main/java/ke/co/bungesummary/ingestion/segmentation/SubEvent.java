package ke.co.bungesummary.ingestion.segmentation;

public record SubEvent(SubEventType type, String text, int lineNumber) {}
