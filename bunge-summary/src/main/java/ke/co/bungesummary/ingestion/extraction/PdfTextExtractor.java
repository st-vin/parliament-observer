package ke.co.bungesummary.ingestion.extraction;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;

/**
 * PDFBox 3.x wrapper for Hansard PDF text extraction with page-number preservation (SRS FR-ING-002).
 */
@Component
public class PdfTextExtractor {

    private static final Pattern PAGE_NUMBER_LINE =
            Pattern.compile("^\\s*\\d{1,4}\\s*$");
    private static final Pattern REPEATED_HEADER =
            Pattern.compile(
                    "^(NATIONAL ASSEMBLY|THE HANSARD|REPUBLIC OF KENYA|PARLIAMENT OF KENYA)\\s*$",
                    Pattern.CASE_INSENSITIVE);

    public PdfExtractionResult extract(Path pdfPath) throws IOException {
        try (InputStream in = Files.newInputStream(pdfPath)) {
            return extract(in);
        }
    }

    public PdfExtractionResult extract(InputStream inputStream) throws IOException {
        byte[] bytes = inputStream.readAllBytes();
        try (PDDocument document = Loader.loadPDF(bytes)) {
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);

            int totalPages = document.getNumberOfPages();
            List<PdfPageText> pages = new ArrayList<>(totalPages);

            for (int page = 1; page <= totalPages; page++) {
                stripper.setStartPage(page);
                stripper.setEndPage(page);
                String raw = stripper.getText(document);
                pages.add(new PdfPageText(page, normalizePageText(raw)));
            }

            return new PdfExtractionResult(List.copyOf(pages));
        }
    }

    String normalizePageText(String raw) {
        if (raw == null || raw.isBlank()) {
            return "";
        }
        String[] lines = raw.split("\\R");
        StringBuilder cleaned = new StringBuilder();
        for (String line : lines) {
            String trimmed = line.strip();
            if (trimmed.isEmpty()) {
                continue;
            }
            if (PAGE_NUMBER_LINE.matcher(trimmed).matches()) {
                continue;
            }
            if (REPEATED_HEADER.matcher(trimmed).matches()) {
                continue;
            }
            if (cleaned.length() > 0) {
                cleaned.append('\n');
            }
            cleaned.append(trimmed);
        }
        return cleaned.toString().strip();
    }
}
