package ke.co.bungesummary.ingestion.extraction;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class PdfTextExtractorTest {

    private static byte[] samplePdfBytes;
    private final PdfTextExtractor extractor = new PdfTextExtractor();

    @BeforeAll
    static void createSamplePdf() throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDType1Font font = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
            for (int i = 0; i < 3; i++) {
                PDPage page = new PDPage();
                document.addPage(page);
                try (PDPageContentStream stream = new PDPageContentStream(document, page)) {
                    stream.beginText();
                    stream.setFont(font, 12);
                    stream.newLineAtOffset(50, 700);
                    stream.showText("SECOND READING — sample Hansard page " + (i + 1));
                    stream.endText();
                }
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            document.save(out);
            samplePdfBytes = out.toByteArray();
        }
    }

    @Test
    void extractFromClasspathSamplePdf_hasPositivePageCount() throws IOException {
        PdfExtractionResult result =
                extractor.extract(new ByteArrayInputStream(samplePdfBytes));

        assertThat(result.pageCount()).isGreaterThan(0);
        assertThat(result.pages()).hasSize(3);
    }

    @Test
    void extract_yieldsNonEmptyTextForMostPages() throws IOException {
        PdfExtractionResult result =
                extractor.extract(new ByteArrayInputStream(samplePdfBytes));

        double ratio = (double) result.pagesWithContent() / result.pageCount();
        assertThat(ratio).isGreaterThanOrEqualTo(0.9);
        assertThat(result.fullText()).contains("SECOND READING");
    }
}
