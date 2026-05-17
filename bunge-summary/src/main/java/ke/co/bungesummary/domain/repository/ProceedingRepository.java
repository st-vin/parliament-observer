package ke.co.bungesummary.domain.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import ke.co.bungesummary.domain.entity.Proceeding;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProceedingRepository extends JpaRepository<Proceeding, UUID> {

    List<Proceeding> findBySittingIdOrderBySequenceInSittingAsc(UUID sittingId);

    long countBySittingId(UUID sittingId);

    @Query(
            """
            SELECT DISTINCT p FROM Proceeding p
            JOIN FETCH p.sitting s
            LEFT JOIN p.proceedingTopics pt
            LEFT JOIN pt.topic t
            LEFT JOIN p.contributions c
            WHERE (:sittingId IS NULL OR s.id = :sittingId)
              AND (:topicSlug IS NULL OR t.slug = :topicSlug)
              AND (:dateFrom IS NULL OR s.date >= :dateFrom)
              AND (:dateTo IS NULL OR s.date <= :dateTo)
              AND (:memberId IS NULL OR c.member.id = :memberId)
            ORDER BY s.date DESC, p.sequenceInSitting ASC
            """)
    Page<Proceeding> findFiltered(
            @Param("sittingId") UUID sittingId,
            @Param("topicSlug") String topicSlug,
            @Param("dateFrom") LocalDate dateFrom,
            @Param("dateTo") LocalDate dateTo,
            @Param("memberId") UUID memberId,
            Pageable pageable);

    @Query(
            """
            SELECT DISTINCT p FROM Proceeding p
            JOIN FETCH p.sitting s
            LEFT JOIN FETCH p.proceedingTopics pt
            LEFT JOIN FETCH pt.topic t
            WHERE t.slug = :topicSlug
            ORDER BY s.date DESC, p.sequenceInSitting ASC
            """)
    Page<Proceeding> findByTopicSlug(@Param("topicSlug") String topicSlug, Pageable pageable);
}
