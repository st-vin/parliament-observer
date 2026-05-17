package ke.co.bungesummary.ingestion.segmentation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Layer-1 proceeding chunk before AI enrichment (SAD §4.1). */
public class RawSegment {

    private final String header;
    private final int startLine;
    private final StringBuilder text = new StringBuilder();
    private final List<SubEvent> subEvents = new ArrayList<>();
    private double confidenceScore = 0.9;
    private boolean needsReview;

    public RawSegment(String header, int startLine) {
        this.header = header;
        this.startLine = startLine;
    }

    public RawSegment(String header, int startLine, double confidenceScore, boolean needsReview) {
        this(header, startLine);
        this.confidenceScore = confidenceScore;
        this.needsReview = needsReview;
    }

    public void appendLine(String line) {
        if (text.length() > 0) {
            text.append('\n');
        }
        text.append(line);
    }

    public void addSubEvent(SubEventType type, String line, int lineNumber) {
        subEvents.add(new SubEvent(type, line, lineNumber));
    }

    public String getHeader() {
        return header;
    }

    public int getStartLine() {
        return startLine;
    }

    public String getText() {
        return text.toString();
    }

    public String fullText() {
        String body = text.toString();
        if (body.isBlank()) {
            return header;
        }
        return header + "\n" + body;
    }

    public List<SubEvent> getSubEvents() {
        return Collections.unmodifiableList(subEvents);
    }

    public double getConfidenceScore() {
        return confidenceScore;
    }

    public void setConfidenceScore(double confidenceScore) {
        this.confidenceScore = confidenceScore;
    }

    public boolean isNeedsReview() {
        return needsReview;
    }

    public void setNeedsReview(boolean needsReview) {
        this.needsReview = needsReview;
    }
}
