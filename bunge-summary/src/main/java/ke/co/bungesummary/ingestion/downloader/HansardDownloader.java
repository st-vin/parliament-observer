package ke.co.bungesummary.ingestion.downloader;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Discovers and downloads Hansard PDFs from parliament.go.ke (SRS FR-ING-001).
 */
@Component
public class HansardDownloader {

    private static final Pattern PDF_HREF =
            Pattern.compile(
                    "href=[\"']([^\"']+\\.pdf[^\"']*)[\"']",
                    Pattern.CASE_INSENSITIVE);

    private final HttpClient httpClient;
    private final String parliamentUrl;
    private final Path storagePath;

    public HansardDownloader(
            @Value("${hansard.parliament-url}") String parliamentUrl,
            @Value("${hansard.pdf-storage-path}") String pdfStoragePath) {
        this.parliamentUrl = parliamentUrl;
        this.storagePath = Path.of(pdfStoragePath);
        this.httpClient =
                HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(30)).build();
    }

    public List<String> discoverPdfUrls(LocalDate from, LocalDate to) throws IOException, InterruptedException {
        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(URI.create(parliamentUrl))
                        .timeout(Duration.ofSeconds(60))
                        .GET()
                        .build();
        HttpResponse<String> response =
                httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new IOException("Hansard index returned HTTP " + response.statusCode());
        }

        return parsePdfUrlsFromHtml(response.body());
    }

    List<String> parsePdfUrlsFromHtml(String html) {
        Set<String> urls = new LinkedHashSet<>();
        Matcher matcher = PDF_HREF.matcher(html);
        while (matcher.find()) {
            urls.add(toAbsoluteUrl(matcher.group(1)));
        }
        return new ArrayList<>(urls);
    }

    public Path download(String pdfUrl, LocalDate sittingDate) throws IOException, InterruptedException {
        Files.createDirectories(storagePath);
        String fileName = sittingDate + ".pdf";
        Path target = storagePath.resolve(fileName);

        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(URI.create(pdfUrl))
                        .timeout(Duration.ofMinutes(2))
                        .GET()
                        .build();
        HttpResponse<byte[]> response =
                httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());
        if (response.statusCode() != 200) {
            throw new IOException("PDF download failed: HTTP " + response.statusCode());
        }
        Files.write(target, response.body());
        return target;
    }

    private String toAbsoluteUrl(String href) {
        if (href.startsWith("http://") || href.startsWith("https://")) {
            return href;
        }
        if (href.startsWith("/")) {
            return "https://www.parliament.go.ke" + href;
        }
        return parliamentUrl.replaceAll("/$", "") + "/" + href;
    }
}
