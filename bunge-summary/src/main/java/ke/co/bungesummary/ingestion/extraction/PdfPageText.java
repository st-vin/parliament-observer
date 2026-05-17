package ke.co.bungesummary.ingestion.extraction;

/**
 * Text extracted from a single PDF page with 1-based page index for source citation (SRS FR-ING-002).
 */
public record PdfPageText(int pageNumber, String text) {

    public boolean hasContent() {
        return text != null && !text.isBlank();
    }
}
