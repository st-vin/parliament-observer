package ke.co.bungesummary.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "proceedings")
public class Proceeding {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sitting_id", nullable = false)
    private Sitting sitting;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String title;

    @Column(name = "proceeding_type", nullable = false, length = 50)
    private String proceedingType;

    @Column(length = 50)
    private String stage;

    @Column(length = 50)
    private String outcome;

    @Column(name = "plain_summary", columnDefinition = "TEXT")
    private String plainSummary;

    @Column(name = "raw_text", nullable = false, columnDefinition = "TEXT")
    private String rawText;

    @Column(name = "source_page_start")
    private Integer sourcePageStart;

    @Column(name = "source_page_end")
    private Integer sourcePageEnd;

    @Column(name = "sequence_in_sitting")
    private Integer sequenceInSitting;

    @Column(name = "confidence_score", precision = 3, scale = 2)
    private BigDecimal confidenceScore;

    @Column(name = "needs_review", nullable = false)
    private boolean needsReview = false;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "proceeding")
    private List<Contribution> contributions = new ArrayList<>();

    @OneToMany(mappedBy = "proceeding")
    private List<ProceedingTopic> proceedingTopics = new ArrayList<>();

    @OneToMany(mappedBy = "proceeding")
    private List<ProceedingChunk> chunks = new ArrayList<>();

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Sitting getSitting() {
        return sitting;
    }

    public void setSitting(Sitting sitting) {
        this.sitting = sitting;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getProceedingType() {
        return proceedingType;
    }

    public void setProceedingType(String proceedingType) {
        this.proceedingType = proceedingType;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public String getOutcome() {
        return outcome;
    }

    public void setOutcome(String outcome) {
        this.outcome = outcome;
    }

    public String getPlainSummary() {
        return plainSummary;
    }

    public void setPlainSummary(String plainSummary) {
        this.plainSummary = plainSummary;
    }

    public String getRawText() {
        return rawText;
    }

    public void setRawText(String rawText) {
        this.rawText = rawText;
    }

    public Integer getSourcePageStart() {
        return sourcePageStart;
    }

    public void setSourcePageStart(Integer sourcePageStart) {
        this.sourcePageStart = sourcePageStart;
    }

    public Integer getSourcePageEnd() {
        return sourcePageEnd;
    }

    public void setSourcePageEnd(Integer sourcePageEnd) {
        this.sourcePageEnd = sourcePageEnd;
    }

    public Integer getSequenceInSitting() {
        return sequenceInSitting;
    }

    public void setSequenceInSitting(Integer sequenceInSitting) {
        this.sequenceInSitting = sequenceInSitting;
    }

    public BigDecimal getConfidenceScore() {
        return confidenceScore;
    }

    public void setConfidenceScore(BigDecimal confidenceScore) {
        this.confidenceScore = confidenceScore;
    }

    public boolean isNeedsReview() {
        return needsReview;
    }

    public void setNeedsReview(boolean needsReview) {
        this.needsReview = needsReview;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<Contribution> getContributions() {
        return contributions;
    }

    public void setContributions(List<Contribution> contributions) {
        this.contributions = contributions;
    }

    public List<ProceedingTopic> getProceedingTopics() {
        return proceedingTopics;
    }

    public void setProceedingTopics(List<ProceedingTopic> proceedingTopics) {
        this.proceedingTopics = proceedingTopics;
    }

    public List<ProceedingChunk> getChunks() {
        return chunks;
    }

    public void setChunks(List<ProceedingChunk> chunks) {
        this.chunks = chunks;
    }
}
