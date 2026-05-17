package ke.co.bungesummary.ingestion.batch;

import java.io.IOException;
import ke.co.bungesummary.domain.entity.Sitting;
import org.springframework.batch.item.ItemProcessor;

public class ProceedingItemProcessor implements ItemProcessor<Sitting, Sitting> {

    private final SittingIngestionService sittingIngestionService;

    public ProceedingItemProcessor(SittingIngestionService sittingIngestionService) {
        this.sittingIngestionService = sittingIngestionService;
    }

    @Override
    public Sitting process(Sitting sitting) throws IOException {
        sittingIngestionService.ingest(sitting);
        return sitting;
    }
}
