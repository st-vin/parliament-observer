package ke.co.bungesummary.domain.repository;

import java.util.List;
import java.util.UUID;
import ke.co.bungesummary.domain.entity.IngestionLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IngestionLogRepository extends JpaRepository<IngestionLog, UUID> {

    List<IngestionLog> findBySittingIdOrderByStartedAtDesc(UUID sittingId);
}
