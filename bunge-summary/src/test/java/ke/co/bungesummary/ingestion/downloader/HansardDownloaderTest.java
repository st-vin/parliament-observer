package ke.co.bungesummary.ingestion.downloader;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

class HansardDownloaderTest {

    private final HansardDownloader downloader =
            new HansardDownloader(
                    "https://www.parliament.go.ke/the-national-assembly/house-business/hansard",
                    "./target/test-hansard");

    @Test
    void discoverPdfUrls_parsesLinksFromHtml() {
        String html =
                """
                <a href="/sites/default/files/hansard/2025-05-01.pdf">May sitting</a>
                <a href="https://www.parliament.go.ke/files/hansard/2025-05-02.pdf">Another</a>
                """;

        List<String> urls = downloader.parsePdfUrlsFromHtml(html);

        assertThat(urls).hasSize(2);
        assertThat(urls.get(0)).endsWith("2025-05-01.pdf");
        assertThat(urls.get(1)).contains("2025-05-02.pdf");
    }

}
