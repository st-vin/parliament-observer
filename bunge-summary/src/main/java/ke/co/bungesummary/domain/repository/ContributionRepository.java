package ke.co.bungesummary.domain.repository;

import java.util.List;
import java.util.UUID;
import ke.co.bungesummary.domain.entity.Contribution;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContributionRepository extends JpaRepository<Contribution, UUID> {

    List<Contribution> findByProceedingIdOrderBySequenceInProceedingAsc(UUID proceedingId);

    List<Contribution> findByMemberIdOrderByCreatedAtDesc(UUID memberId);
}
