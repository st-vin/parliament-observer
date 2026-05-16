package ke.co.bungesummary.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class ProceedingTopicId implements Serializable {

    @Column(name = "proceeding_id")
    private UUID proceedingId;

    @Column(name = "topic_id")
    private UUID topicId;

    public ProceedingTopicId() {}

    public ProceedingTopicId(UUID proceedingId, UUID topicId) {
        this.proceedingId = proceedingId;
        this.topicId = topicId;
    }

    public UUID getProceedingId() {
        return proceedingId;
    }

    public void setProceedingId(UUID proceedingId) {
        this.proceedingId = proceedingId;
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
        if (!(o instanceof ProceedingTopicId that)) {
            return false;
        }
        return Objects.equals(proceedingId, that.proceedingId)
                && Objects.equals(topicId, that.topicId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(proceedingId, topicId);
    }
}
