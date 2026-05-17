package ke.co.bungesummary.ingestion.segmentation;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

/**
 * Layer-1 rule-based Hansard segmenter (SAD §4.1). Identifies proceeding boundaries and speaker turns.
 */
@Component
public class HansardSegmenter {

    private static final List<String> PROCEEDING_MARKERS =
            List.of(
                    "FIRST READING",
                    "SECOND READING",
                    "THIRD READING",
                    "COMMITTEE OF THE WHOLE HOUSE",
                    "REPORT STAGE",
                    "MOTION",
                    "ADJOURNMENT MOTION",
                    "PETITION",
                    "STATEMENT",
                    "MINISTERIAL STATEMENT",
                    "ORAL QUESTIONS",
                    "QUESTIONS FOR WRITTEN REPLY",
                    "PAPERS",
                    "NOTICE OF MOTION");

    private static final Pattern POINT_OF_ORDER_PATTERN =
            Pattern.compile(
                    "^\\[?\\s*(Point of Order|POINT OF ORDER)\\s*\\]?\\s*$",
                    Pattern.CASE_INSENSITIVE);

    private static final Pattern MEMBER_PATTERN =
            Pattern.compile("^Hon\\.\\s[\\w\\s']+\\s\\([\\w\\s]+\\):");

    public SegmentationResult segment(String fullText) {
        List<RawSegment> segments = new ArrayList<>();
        String[] lines = fullText.split("\\R");

        RawSegment current = null;
        int lineNum = 0;
        boolean seenProceedingMarker = false;

        for (String line : lines) {
            lineNum++;
            String trimmed = line.strip();
            if (trimmed.isEmpty()) {
                continue;
            }

            if (isProceedingBoundary(trimmed)) {
                seenProceedingMarker = true;
                if (current != null) {
                    segments.add(current);
                }
                current = new RawSegment(trimmed, lineNum);

            } else if (isPointOfOrder(trimmed) && current != null) {
                current.addSubEvent(SubEventType.POINT_OF_ORDER, trimmed, lineNum);

            } else if (isDivision(trimmed) && current != null) {
                current.addSubEvent(SubEventType.DIVISION, trimmed, lineNum);

            } else if (current != null) {
                current.appendLine(trimmed);

            } else if (!seenProceedingMarker && MEMBER_PATTERN.matcher(trimmed).find()) {
                current = new RawSegment("UNMARKED PROCEEDING", lineNum, 0.45, true);
                current.appendLine(trimmed);
            }
        }

        if (current != null) {
            if (!seenProceedingMarker && !current.isNeedsReview()) {
                current.setConfidenceScore(0.5);
                current.setNeedsReview(true);
            }
            segments.add(current);
        }

        return new SegmentationResult(List.copyOf(segments));
    }

    private boolean isProceedingBoundary(String line) {
        String upper = line.toUpperCase();
        return PROCEEDING_MARKERS.stream().anyMatch(upper::startsWith);
    }

    private boolean isPointOfOrder(String line) {
        return POINT_OF_ORDER_PATTERN.matcher(line.strip()).matches();
    }

    private boolean isDivision(String line) {
        String upper = line.trim().toUpperCase();
        return upper.startsWith("DIVISION") || line.contains("Ayes") || line.contains("Noes");
    }
}
