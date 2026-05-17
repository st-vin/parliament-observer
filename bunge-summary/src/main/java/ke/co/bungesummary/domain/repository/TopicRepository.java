package ke.co.bungesummary.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import ke.co.bungesummary.domain.entity.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TopicRepository extends JpaRepository<Topic, UUID> {

    Optional<Topic> findBySlug(String slug);

    List<Topic> findAllByOrderByNameAsc();

    @Query(
            """
            SELECT t.id, COUNT(pt)
            FROM Topic t
            LEFT JOIN ProceedingTopic pt ON pt.topic.id = t.id
            GROUP BY t.id
            """)
    List<Object[]> countProceedingsByTopic();
}
