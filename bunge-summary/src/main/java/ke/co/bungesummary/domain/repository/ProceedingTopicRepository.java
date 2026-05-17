package ke.co.bungesummary.domain.repository;

import java.util.List;
import java.util.UUID;
import ke.co.bungesummary.domain.entity.ProceedingTopic;
import ke.co.bungesummary.domain.entity.ProceedingTopicId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProceedingTopicRepository extends JpaRepository<ProceedingTopic, ProceedingTopicId> {

    List<ProceedingTopic> findByTopicId(UUID topicId);

    @Query(
            """
            SELECT pt FROM ProceedingTopic pt
            JOIN FETCH pt.topic
            WHERE pt.proceeding.id = :proceedingId
            """)
    List<ProceedingTopic> findByProceedingId(@Param("proceedingId") UUID proceedingId);
}
