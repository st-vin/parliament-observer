package ke.co.bungesummary.domain.repository;

import java.util.Optional;
import java.util.UUID;
import ke.co.bungesummary.domain.entity.Topic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TopicRepository extends JpaRepository<Topic, UUID> {

    Optional<Topic> findBySlug(String slug);
}
