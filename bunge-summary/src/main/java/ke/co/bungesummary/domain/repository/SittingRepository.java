package ke.co.bungesummary.domain.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import ke.co.bungesummary.domain.entity.Sitting;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SittingRepository extends JpaRepository<Sitting, UUID> {

    Optional<Sitting> findByDate(LocalDate date);

    Optional<Sitting> findTopByOrderByDateDesc();

    List<Sitting> findByIngestionStatusOrderByDateAsc(String ingestionStatus);

    @Query(
            """
            SELECT s FROM Sitting s
            WHERE (:year IS NULL OR YEAR(s.date) = :year)
              AND (:month IS NULL OR MONTH(s.date) = :month)
            ORDER BY s.date DESC
            """)
    Page<Sitting> findFiltered(
            @Param("year") Integer year, @Param("month") Integer month, Pageable pageable);
}
