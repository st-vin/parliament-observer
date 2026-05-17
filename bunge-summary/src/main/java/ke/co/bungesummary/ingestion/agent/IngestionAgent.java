package ke.co.bungesummary.ingestion.agent;

import com.pgvector.PGvector;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import ke.co.bungesummary.domain.entity.Contribution;
import ke.co.bungesummary.domain.entity.Member;
import ke.co.bungesummary.domain.entity.Proceeding;
import ke.co.bungesummary.domain.entity.ProceedingChunk;
import ke.co.bungesummary.domain.entity.ProceedingTopic;
import ke.co.bungesummary.domain.entity.ProceedingTopicId;
import ke.co.bungesummary.domain.entity.Sitting;
import ke.co.bungesummary.domain.entity.Topic;
import ke.co.bungesummary.domain.repository.ContributionRepository;
import ke.co.bungesummary.domain.repository.MemberRepository;
import ke.co.bungesummary.domain.repository.ProceedingChunkRepository;
import ke.co.bungesummary.domain.repository.ProceedingRepository;
import ke.co.bungesummary.domain.repository.ProceedingTopicRepository;
import ke.co.bungesummary.domain.repository.TopicRepository;
import ke.co.bungesummary.ingestion.segmentation.RawSegment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@ConditionalOnBean(ProceedingRepository.class)
public class IngestionAgent {

    private static final Logger log = LoggerFactory.getLogger(IngestionAgent.class);

    private static final String TOPIC_TAXONOMY =
            """
            agriculture, health, education, finance-budget, employment, youth, infrastructure,
            security, environment, governance, gender-social, trade-economy, energy, land,
            legal-constitutional
            """;

    private final ClassificationTool classificationTool;
    private final SummaryTool summaryTool;
    private final ContributionTool contributionTool;
    private final EmbeddingTool embeddingTool;
    private final ProceedingRepository proceedingRepository;
    private final ProceedingTopicRepository proceedingTopicRepository;
    private final ProceedingChunkRepository proceedingChunkRepository;
    private final ContributionRepository contributionRepository;
    private final TopicRepository topicRepository;
    private final MemberRepository memberRepository;

    public IngestionAgent(
            ObjectProvider<ClassificationTool> classificationTool,
            ObjectProvider<SummaryTool> summaryTool,
            ObjectProvider<ContributionTool> contributionTool,
            ObjectProvider<EmbeddingTool> embeddingTool,
            ProceedingRepository proceedingRepository,
            ProceedingTopicRepository proceedingTopicRepository,
            ProceedingChunkRepository proceedingChunkRepository,
            ContributionRepository contributionRepository,
            TopicRepository topicRepository,
            MemberRepository memberRepository) {
        this.classificationTool = classificationTool.getIfAvailable();
        this.summaryTool = summaryTool.getIfAvailable();
        this.contributionTool = contributionTool.getIfAvailable();
        this.embeddingTool = embeddingTool.getIfAvailable();
        this.proceedingRepository = proceedingRepository;
        this.proceedingTopicRepository = proceedingTopicRepository;
        this.proceedingChunkRepository = proceedingChunkRepository;
        this.contributionRepository = contributionRepository;
        this.topicRepository = topicRepository;
        this.memberRepository = memberRepository;
    }

    @Transactional
    public void processSegments(Sitting sitting, List<RawSegment> segments) {
        for (int i = 0; i < segments.size(); i++) {
            try {
                processSegment(sitting, segments.get(i), i + 1);
            } catch (Exception e) {
                log.warn(
                        "Failed to process segment {} for sitting {}: {}",
                        i + 1,
                        sitting.getId(),
                        e.getMessage());
            }
        }
    }

    @Transactional
    public Proceeding processSegment(Sitting sitting, RawSegment segment, int sequence) {
        String text = segment.fullText();
        Proceeding proceeding = new Proceeding();
        proceeding.setSitting(sitting);
        proceeding.setTitle(segment.getHeader());
        proceeding.setProceedingType(inferProceedingType(segment.getHeader()));
        proceeding.setRawText(text);
        proceeding.setSequenceInSitting(sequence);
        proceeding.setNeedsReview(segment.isNeedsReview());
        proceeding.setConfidenceScore(
                BigDecimal.valueOf(segment.getConfidenceScore()).setScale(2, RoundingMode.HALF_UP));

        if (summaryTool != null) {
            try {
                SummaryResult summary = summaryTool.summarize(text);
                proceeding.setPlainSummary(summary.plainSummary());
                proceeding.setTitle(extractTitle(segment.getHeader(), summary.plainSummary()));
            } catch (Exception e) {
                log.warn("Summary failed for segment {}: {}", sequence, e.getMessage());
            }
        }

        proceeding = proceedingRepository.save(proceeding);

        if (classificationTool != null) {
            try {
                persistTopics(proceeding, classificationTool.classify(text, TOPIC_TAXONOMY));
            } catch (Exception e) {
                log.warn("Classification failed for proceeding {}: {}", proceeding.getId(), e.getMessage());
            }
        }

        if (contributionTool != null) {
            try {
                persistContributions(proceeding, contributionTool.extractContributions(text));
            } catch (Exception e) {
                log.warn("Contribution extraction failed: {}", e.getMessage());
            }
        }

        if (embeddingTool != null) {
            try {
                persistChunk(proceeding, text, embeddingTool.embed(text));
            } catch (Exception e) {
                log.warn("Embedding failed for proceeding {}: {}", proceeding.getId(), e.getMessage());
            }
        }

        return proceeding;
    }

    private void persistTopics(Proceeding proceeding, List<TopicClassification> classifications) {
        for (TopicClassification classification : classifications) {
            Optional<Topic> topic = topicRepository.findBySlug(classification.slug());
            if (topic.isEmpty()) {
                continue;
            }
            ProceedingTopic link = new ProceedingTopic();
            link.setId(new ProceedingTopicId(proceeding.getId(), topic.get().getId()));
            link.setProceeding(proceeding);
            link.setTopic(topic.get());
            link.setConfidenceScore(
                    BigDecimal.valueOf(classification.confidence()).setScale(2, RoundingMode.HALF_UP));
            proceedingTopicRepository.save(link);
        }
    }

    private void persistContributions(
            Proceeding proceeding, List<ContributionExtraction> extractions) {
        int sequence = 1;
        for (ContributionExtraction extraction : extractions) {
            Member member = resolveMember(extraction.memberName());
            Contribution contribution = new Contribution();
            contribution.setProceeding(proceeding);
            contribution.setMember(member);
            contribution.setSummary(extraction.summary());
            contribution.setStance(normalizeStance(extraction.stance()));
            contribution.setVerbatimExcerpt(extraction.verbatimExcerpt());
            contribution.setStanceConfidence(
                    BigDecimal.valueOf(extraction.stanceConfidence()).setScale(2, RoundingMode.HALF_UP));
            contribution.setSequenceInProceeding(sequence++);
            contributionRepository.save(contribution);
        }
    }

    private void persistChunk(Proceeding proceeding, String text, float[] vector) {
        ProceedingChunk chunk = new ProceedingChunk();
        chunk.setProceeding(proceeding);
        chunk.setChunkText(text);
        chunk.setChunkIndex(0);
        chunk.setEmbedding(new PGvector(vector));
        proceedingChunkRepository.save(chunk);
    }

    private Member resolveMember(String fullName) {
        return memberRepository
                .findByFullNameIgnoreCase(fullName)
                .orElseGet(
                        () -> {
                            Member member = new Member();
                            member.setFullName(fullName);
                            member.setDisplayName(fullName);
                            member.setChamber("NATIONAL_ASSEMBLY");
                            return memberRepository.save(member);
                        });
    }

    private static String normalizeStance(String stance) {
        if (stance == null) {
            return "UNCERTAIN";
        }
        String upper = stance.trim().toUpperCase();
        return switch (upper) {
            case "FOR", "AGAINST", "NEUTRAL", "UNCERTAIN" -> upper;
            case "ABSTAINED" -> "NEUTRAL";
            default -> "UNCERTAIN";
        };
    }

    private static String inferProceedingType(String header) {
        String upper = header.toUpperCase();
        if (upper.contains("READING")) {
            return "BILL_READING";
        }
        if (upper.contains("QUESTION")) {
            return "ORAL_QUESTIONS";
        }
        if (upper.startsWith("MOTION")) {
            return "MOTION";
        }
        return "OTHER";
    }

    private static String extractTitle(String header, String summary) {
        if (header != null && !header.equals("UNMARKED PROCEEDING") && header.length() > 5) {
            return header.length() > 200 ? header.substring(0, 200) : header;
        }
        if (summary != null && summary.length() > 20) {
            int end = Math.min(summary.length(), 120);
            return summary.substring(0, end).strip() + "...";
        }
        return header;
    }
}
