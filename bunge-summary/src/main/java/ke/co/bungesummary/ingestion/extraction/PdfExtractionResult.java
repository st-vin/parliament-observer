package ke.co.bungesummary.ingestion.extraction;

import java.util.List;
import java.util.stream.Collectors;

/** Full PDF extraction output: per-page blocks plus concatenated text for segmentation. */
public record PdfExtractionResult(List<PdfPageText> pages) {

    public String fullText() {
        return pages.stream().map(PdfPageText::text).collect(Collectors.joining("\n\n"));
    }

    public int pageCount() {
        return pages.size();
    }

    public long pagesWithContent() {
        return pages.stream().filter(PdfPageText::hasContent).count();
    }
}
