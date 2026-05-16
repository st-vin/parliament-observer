package ke.co.bungesummary.domain.repository;

import java.util.Optional;
import java.util.UUID;
import ke.co.bungesummary.domain.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, UUID> {

    Optional<Member> findByFullNameIgnoreCase(String fullName);
}
