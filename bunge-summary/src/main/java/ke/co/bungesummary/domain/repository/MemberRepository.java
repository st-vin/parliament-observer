package ke.co.bungesummary.domain.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import ke.co.bungesummary.domain.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberRepository extends JpaRepository<Member, UUID> {

    Optional<Member> findByFullNameIgnoreCase(String fullName);

    @Query(
            """
            SELECT m FROM Member m
            WHERE (:constituency IS NULL OR LOWER(m.constituency) = LOWER(:constituency))
              AND (:party IS NULL OR LOWER(m.party) = LOWER(:party))
              AND (:chamber IS NULL OR m.chamber = :chamber)
            ORDER BY m.fullName ASC
            """)
    Page<Member> findFiltered(
            @Param("constituency") String constituency,
            @Param("party") String party,
            @Param("chamber") String chamber,
            Pageable pageable);

    @Query(
            """
            SELECT c FROM Contribution c
            JOIN FETCH c.proceeding p
            JOIN FETCH p.sitting s
            JOIN FETCH c.member m
            LEFT JOIN p.proceedingTopics pt
            LEFT JOIN pt.topic t
            WHERE m.id = :memberId
              AND (:topicSlug IS NULL OR t.slug = :topicSlug)
              AND (:dateFrom IS NULL OR s.date >= :dateFrom)
              AND (:dateTo IS NULL OR s.date <= :dateTo)
            ORDER BY s.date DESC, c.sequenceInProceeding ASC
            """)
    Page<ke.co.bungesummary.domain.entity.Contribution> findMemberContributions(
            @Param("memberId") UUID memberId,
            @Param("topicSlug") String topicSlug,
            @Param("dateFrom") LocalDate dateFrom,
            @Param("dateTo") LocalDate dateTo,
            Pageable pageable);
}
