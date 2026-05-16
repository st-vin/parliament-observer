package ke.co.bungesummary.domain.repository;

import java.util.List;
import java.util.UUID;
import ke.co.bungesummary.domain.entity.ProceedingTopic;
import ke.co.bungesummary.domain.entity.ProceedingTopicId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProceedingTopicRepository extends JpaRepository<ProceedingTopic, ProceedingTopicId> {

    List<ProceedingTopic> findByTopicId(UUID topicId);
}
