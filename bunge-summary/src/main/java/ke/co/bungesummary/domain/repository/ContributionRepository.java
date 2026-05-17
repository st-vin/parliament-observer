package ke.co.bungesummary.domain.repository;

import java.util.List;
import java.util.UUID;
import ke.co.bungesummary.domain.entity.Contribution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ContributionRepository extends JpaRepository<Contribution, UUID> {

    @Query(
            """
            SELECT c FROM Contribution c
            JOIN FETCH c.member
            WHERE c.proceeding.id = :proceedingId
            ORDER BY c.sequenceInProceeding ASC
            """)
    List<Contribution> findByProceedingIdOrderBySequenceInProceedingAsc(
            @Param("proceedingId") UUID proceedingId);

    List<Contribution> findByMemberIdOrderByCreatedAtDesc(UUID memberId);
}
