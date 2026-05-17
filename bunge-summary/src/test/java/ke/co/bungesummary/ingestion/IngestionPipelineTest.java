package ke.co.bungesummary.ingestion;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import ke.co.bungesummary.ingestion.extraction.PdfExtractionResult;
import ke.co.bungesummary.ingestion.extraction.PdfTextExtractor;
import ke.co.bungesummary.ingestion.segmentation.HansardSegmenter;
import ke.co.bungesummary.ingestion.segmentation.SegmentationResult;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.junit.jupiter.api.Test;

/** End-to-end unit test: PDF extract → segment (mocked Gemini pipeline per Test Plan). */
class IngestionPipelineTest {

    private final PdfTextExtractor extractor = new PdfTextExtractor();
    private final HansardSegmenter segmenter = new HansardSegmenter();

    @Test
    void extractAndSegment_producesProceedingChunks() throws Exception {
        byte[] pdf = sampleHansardPdf();
        PdfExtractionResult extraction = extractor.extract(new ByteArrayInputStream(pdf));
        SegmentationResult segmentation = segmenter.segment(extraction.fullText());

        assertThat(extraction.pageCount()).isPositive();
        assertThat(segmentation.segmentCount()).isGreaterThanOrEqualTo(1);
        assertThat(segmentation.segments().getFirst().getHeader()).contains("SECOND READING");
    }

    private static byte[] sampleHansardPdf() throws Exception {
        try (PDDocument document = new PDDocument();
                var out = new java.io.ByteArrayOutputStream()) {
            PDPage page = new PDPage();
            document.addPage(page);
            PDType1Font font = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
            try (PDPageContentStream stream = new PDPageContentStream(document, page)) {
                stream.beginText();
                stream.setFont(font, 11);
                stream.newLineAtOffset(40, 700);
                stream.showText("SECOND READING");
                stream.newLineAtOffset(0, -20);
                stream.showText("THE FINANCE BILL");
                stream.newLineAtOffset(0, -20);
                stream.showText("Hon. KIMANI (Kikuyu): We debate the Finance Bill today.");
                stream.endText();
            }
            document.save(out);
            return out.toByteArray();
        }
    }
}
