package ke.co.bungesummary.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class UserTopicSubscriptionId implements Serializable {

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "topic_id")
    private UUID topicId;

    public UserTopicSubscriptionId() {}

    public UserTopicSubscriptionId(UUID userId, UUID topicId) {
        this.userId = userId;
        this.topicId = topicId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getTopicId() {
        return topicId;
    }

    public void setTopicId(UUID topicId) {
        this.topicId = topicId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserTopicSubscriptionId that)) {
            return false;
        }
        return Objects.equals(userId, that.userId) && Objects.equals(topicId, that.topicId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, topicId);
    }
}
