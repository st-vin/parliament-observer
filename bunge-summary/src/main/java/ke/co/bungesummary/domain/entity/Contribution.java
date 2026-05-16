package ke.co.bungesummary.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "contributions")
public class Contribution {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "proceeding_id", nullable = false)
    private Proceeding proceeding;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @Column(length = 20)
    private String stance;

    @Column(name = "stance_confidence", precision = 3, scale = 2)
    private BigDecimal stanceConfidence;

    @Column(name = "verbatim_excerpt", columnDefinition = "TEXT")
    private String verbatimExcerpt;

    @Column(name = "excerpt_page")
    private Integer excerptPage;

    @Column(name = "sequence_in_proceeding")
    private Integer sequenceInProceeding;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Proceeding getProceeding() {
        return proceeding;
    }

    public void setProceeding(Proceeding proceeding) {
        this.proceeding = proceeding;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getStance() {
        return stance;
    }

    public void setStance(String stance) {
        this.stance = stance;
    }

    public BigDecimal getStanceConfidence() {
        return stanceConfidence;
    }

    public void setStanceConfidence(BigDecimal stanceConfidence) {
        this.stanceConfidence = stanceConfidence;
    }

    public String getVerbatimExcerpt() {
        return verbatimExcerpt;
    }

    public void setVerbatimExcerpt(String verbatimExcerpt) {
        this.verbatimExcerpt = verbatimExcerpt;
    }

    public Integer getExcerptPage() {
        return excerptPage;
    }

    public void setExcerptPage(Integer excerptPage) {
        this.excerptPage = excerptPage;
    }

    public Integer getSequenceInProceeding() {
        return sequenceInProceeding;
    }

    public void setSequenceInProceeding(Integer sequenceInProceeding) {
        this.sequenceInProceeding = sequenceInProceeding;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
