package ke.co.bungesummary.ingestion.segmentation;

import java.util.List;

public record SegmentationResult(List<RawSegment> segments) {

    public int segmentCount() {
        return segments.size();
    }
}
