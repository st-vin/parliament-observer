package ke.co.bungesummary.domain.repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import ke.co.bungesummary.domain.entity.Sitting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SittingRepository extends JpaRepository<Sitting, UUID> {

    Optional<Sitting> findByDate(LocalDate date);
}
