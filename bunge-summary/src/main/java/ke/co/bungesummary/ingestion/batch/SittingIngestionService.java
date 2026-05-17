package ke.co.bungesummary.ingestion.batch;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import ke.co.bungesummary.domain.entity.IngestionLog;
import ke.co.bungesummary.domain.entity.Sitting;
import ke.co.bungesummary.domain.repository.IngestionLogRepository;
import ke.co.bungesummary.domain.repository.ProceedingRepository;
import ke.co.bungesummary.domain.repository.SittingRepository;
import ke.co.bungesummary.ingestion.agent.IngestionAgent;
import ke.co.bungesummary.ingestion.extraction.PdfExtractionResult;
import ke.co.bungesummary.ingestion.extraction.PdfTextExtractor;
import ke.co.bungesummary.ingestion.segmentation.HansardSegmenter;
import ke.co.bungesummary.ingestion.segmentation.SegmentationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@ConditionalOnBean(ProceedingRepository.class)
public class SittingIngestionService {

    private static final Logger log = LoggerFactory.getLogger(SittingIngestionService.class);

    private final PdfTextExtractor pdfTextExtractor;
    private final HansardSegmenter hansardSegmenter;
    private final IngestionAgent ingestionAgent;
    private final SittingRepository sittingRepository;
    private final ProceedingRepository proceedingRepository;
    private final IngestionLogRepository ingestionLogRepository;

    public SittingIngestionService(
            PdfTextExtractor pdfTextExtractor,
            HansardSegmenter hansardSegmenter,
            IngestionAgent ingestionAgent,
            SittingRepository sittingRepository,
            ProceedingRepository proceedingRepository,
            IngestionLogRepository ingestionLogRepository) {
        this.pdfTextExtractor = pdfTextExtractor;
        this.hansardSegmenter = hansardSegmenter;
        this.ingestionAgent = ingestionAgent;
        this.sittingRepository = sittingRepository;
        this.proceedingRepository = proceedingRepository;
        this.ingestionLogRepository = ingestionLogRepository;
    }

    @Transactional
    public void ingest(Sitting sitting) throws IOException {
        if (proceedingRepository.countBySittingId(sitting.getId()) > 0) {
            log.info("Sitting {} already ingested — skipping (idempotent)", sitting.getId());
            return;
        }

        IngestionLog ingestionLog = new IngestionLog();
        ingestionLog.setSitting(sitting);
        ingestionLog.setStartedAt(LocalDateTime.now());
        ingestionLog.setStatus("RUNNING");
        ingestionLogRepository.save(ingestionLog);

        try {
            sitting.setIngestionStatus("PROCESSING");
            sittingRepository.save(sitting);

            PdfExtractionResult extraction = pdfTextExtractor.extract(Path.of(sitting.getRawPdfPath()));
            SegmentationResult segmentation = hansardSegmenter.segment(extraction.fullText());
            ingestionAgent.processSegments(sitting, segmentation.segments());

            int count = (int) proceedingRepository.countBySittingId(sitting.getId());
            sitting.setIngestionStatus("COMPLETED");
            sitting.setIngestedAt(LocalDateTime.now());
            sittingRepository.save(sitting);

            ingestionLog.setProceedingsExtracted(count);
            ingestionLog.setStatus("COMPLETED");
            ingestionLog.setCompletedAt(LocalDateTime.now());
            ingestionLogRepository.save(ingestionLog);
        } catch (Exception e) {
            sitting.setIngestionStatus("FAILED");
            sittingRepository.save(sitting);
            ingestionLog.setStatus("FAILED");
            ingestionLog.setErrorMessage(e.getMessage());
            ingestionLog.setCompletedAt(LocalDateTime.now());
            ingestionLogRepository.save(ingestionLog);
            throw e;
        }
    }
}
