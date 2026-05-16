package ke.co.bungesummary.domain.repository;

import java.util.Optional;
import java.util.UUID;
import ke.co.bungesummary.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);
}
