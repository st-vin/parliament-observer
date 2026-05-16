package ke.co.bungesummary.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "proceeding_topics")
public class ProceedingTopic {

    @EmbeddedId
    private ProceedingTopicId id = new ProceedingTopicId();

    @MapsId("proceedingId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "proceeding_id", nullable = false)
    private Proceeding proceeding;

    @MapsId("topicId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "topic_id", nullable = false)
    private Topic topic;

    @Column(name = "confidence_score", precision = 3, scale = 2)
    private BigDecimal confidenceScore;

    public ProceedingTopicId getId() {
        return id;
    }

    public void setId(ProceedingTopicId id) {
        this.id = id;
    }

    public Proceeding getProceeding() {
        return proceeding;
    }

    public void setProceeding(Proceeding proceeding) {
        this.proceeding = proceeding;
    }

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    public BigDecimal getConfidenceScore() {
        return confidenceScore;
    }

    public void setConfidenceScore(BigDecimal confidenceScore) {
        this.confidenceScore = confidenceScore;
    }
}
