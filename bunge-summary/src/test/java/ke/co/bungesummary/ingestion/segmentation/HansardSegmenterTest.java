package ke.co.bungesummary.ingestion.segmentation;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class HansardSegmenterTest {

    private final HansardSegmenter segmenter = new HansardSegmenter();

    @Test
    void segment_identifiesMultipleProceedings() {
        String text =
                """
                SECOND READING
                THE AFFORDABLE HOUSING (AMENDMENT) BILL
                Hon. KIMANI ICHUNG'WAH (Kikuyu): Thank you, Mr. Speaker...

                ORAL QUESTIONS
                QUESTION 001 BY HON. ASHA MOHAMED
                Hon. ASHA MOHAMED (Nominated): Thank you, Mr. Speaker...
                """;

        SegmentationResult result = segmenter.segment(text);

        assertThat(result.segmentCount()).isEqualTo(2);
        assertThat(result.segments().get(0).getHeader()).startsWith("SECOND READING");
        assertThat(result.segments().get(0).fullText()).containsIgnoringCase("affordable housing");
        assertThat(result.segments().get(1).getHeader()).startsWith("ORAL QUESTIONS");
    }

    @Test
    void segment_pointOfOrderAttachedToParentProceeding() {
        String text =
                """
                SECOND READING
                THE FINANCE BILL
                Hon. KIMANI ICHUNG'WAH (Kikuyu): This bill addresses...
                [Point of Order]
                Hon. JOHN MBADI (Suba South): On a point of order...
                Hon. KIMANI ICHUNG'WAH (Kikuyu): Continuing, Mr. Speaker...
                """;

        SegmentationResult result = segmenter.segment(text);

        assertThat(result.segmentCount()).isEqualTo(1);
        RawSegment segment = result.segments().getFirst();
        assertThat(segment.getSubEvents()).hasSize(1);
        assertThat(segment.getSubEvents().getFirst().type()).isEqualTo(SubEventType.POINT_OF_ORDER);
    }

    @Test
    void segment_unmarkedBoundaryFlagsNeedsReview() {
        String text =
                """
                Hon. JANE DOE (Nairobi): We must discuss fertiliser subsidies without a header.
                Hon. JOHN SMITH (Mombasa): I agree with the previous speaker.
                """;

        SegmentationResult result = segmenter.segment(text);

        assertThat(result.segmentCount()).isEqualTo(1);
        RawSegment segment = result.segments().getFirst();
        assertThat(segment.isNeedsReview()).isTrue();
        assertThat(segment.getConfidenceScore()).isLessThan(0.6);
    }
}
