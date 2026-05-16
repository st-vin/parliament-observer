package ke.co.bungesummary.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "sittings")
public class Sitting {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private LocalDate date;

    @Column(nullable = false, length = 50)
    private String chamber = "NATIONAL_ASSEMBLY";

    @Column(name = "session_number")
    private Integer sessionNumber;

    @Column(name = "parliament_number")
    private Integer parliamentNumber;

    @Column(name = "raw_pdf_path", nullable = false, columnDefinition = "TEXT")
    private String rawPdfPath;

    @Column(name = "ingestion_status", nullable = false, length = 20)
    private String ingestionStatus = "PENDING";

    @Column(name = "ingested_at")
    private LocalDateTime ingestedAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "sitting")
    private List<Proceeding> proceedings = new ArrayList<>();

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getChamber() {
        return chamber;
    }

    public void setChamber(String chamber) {
        this.chamber = chamber;
    }

    public Integer getSessionNumber() {
        return sessionNumber;
    }

    public void setSessionNumber(Integer sessionNumber) {
        this.sessionNumber = sessionNumber;
    }

    public Integer getParliamentNumber() {
        return parliamentNumber;
    }

    public void setParliamentNumber(Integer parliamentNumber) {
        this.parliamentNumber = parliamentNumber;
    }

    public String getRawPdfPath() {
        return rawPdfPath;
    }

    public void setRawPdfPath(String rawPdfPath) {
        this.rawPdfPath = rawPdfPath;
    }

    public String getIngestionStatus() {
        return ingestionStatus;
    }

    public void setIngestionStatus(String ingestionStatus) {
        this.ingestionStatus = ingestionStatus;
    }

    public LocalDateTime getIngestedAt() {
        return ingestedAt;
    }

    public void setIngestedAt(LocalDateTime ingestedAt) {
        this.ingestedAt = ingestedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<Proceeding> getProceedings() {
        return proceedings;
    }

    public void setProceedings(List<Proceeding> proceedings) {
        this.proceedings = proceedings;
    }
}
