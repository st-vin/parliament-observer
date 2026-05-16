package ke.co.bungesummary.domain.repository;

import java.util.List;
import java.util.UUID;
import ke.co.bungesummary.domain.entity.UserTopicSubscription;
import ke.co.bungesummary.domain.entity.UserTopicSubscriptionId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserTopicSubscriptionRepository
        extends JpaRepository<UserTopicSubscription, UserTopicSubscriptionId> {

    List<UserTopicSubscription> findByUserId(UUID userId);
}
