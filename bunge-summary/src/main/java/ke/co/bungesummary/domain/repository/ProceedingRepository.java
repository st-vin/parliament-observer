package ke.co.bungesummary.domain.repository;

import java.util.List;
import java.util.UUID;
import ke.co.bungesummary.domain.entity.Proceeding;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProceedingRepository extends JpaRepository<Proceeding, UUID> {

    List<Proceeding> findBySittingIdOrderBySequenceInSittingAsc(UUID sittingId);
}
