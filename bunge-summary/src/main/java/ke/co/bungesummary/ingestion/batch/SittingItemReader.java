package ke.co.bungesummary.ingestion.batch;

import java.util.Iterator;
import java.util.List;
import ke.co.bungesummary.domain.entity.Sitting;
import ke.co.bungesummary.domain.repository.SittingRepository;
import org.springframework.batch.item.ItemReader;

public class SittingItemReader implements ItemReader<Sitting> {

    private final Iterator<Sitting> iterator;

    public SittingItemReader(SittingRepository sittingRepository) {
        List<Sitting> pending = sittingRepository.findByIngestionStatusOrderByDateAsc("PENDING");
        this.iterator = pending.iterator();
    }

    @Override
    public Sitting read() {
        return iterator.hasNext() ? iterator.next() : null;
    }
}
