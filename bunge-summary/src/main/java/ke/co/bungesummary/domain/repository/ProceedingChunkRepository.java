package ke.co.bungesummary.domain.repository;

import java.util.List;
import java.util.UUID;
import ke.co.bungesummary.domain.entity.ProceedingChunk;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProceedingChunkRepository extends JpaRepository<ProceedingChunk, UUID> {

    List<ProceedingChunk> findByProceedingIdOrderByChunkIndexAsc(UUID proceedingId);
}
