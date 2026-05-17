package ke.co.bungesummary.ingestion.batch;

import ke.co.bungesummary.domain.entity.Sitting;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

/** No-op writer — persistence handled in {@link SittingIngestionService}. */
public class ProceedingItemWriter implements ItemWriter<Sitting> {

    @Override
    public void write(Chunk<? extends Sitting> chunk) {
        // Sitting status updated during ingestion
    }
}
