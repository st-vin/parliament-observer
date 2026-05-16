package ke.co.bungesummary.db;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.EntityManager;
import jakarta.persistence.metamodel.EntityType;
import java.util.Set;
import java.util.stream.Collectors;
import ke.co.bungesummary.domain.entity.Contribution;
import ke.co.bungesummary.domain.entity.IngestionLog;
import ke.co.bungesummary.domain.entity.Member;
import ke.co.bungesummary.domain.entity.Proceeding;
import ke.co.bungesummary.domain.entity.ProceedingChunk;
import ke.co.bungesummary.domain.entity.Sitting;
import ke.co.bungesummary.domain.entity.Topic;
import ke.co.bungesummary.domain.entity.User;
import ke.co.bungesummary.domain.entity.UserTopicSubscription;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@ActiveProfiles("flyway-it")
@Testcontainers(disabledWithoutDocker = true)
class JpaSchemaValidationTest {

  private static final Set<String> REQUIRED_ENTITIES =
      Set.of(
          Sitting.class.getSimpleName(),
          Proceeding.class.getSimpleName(),
          Topic.class.getSimpleName(),
          Member.class.getSimpleName(),
          Contribution.class.getSimpleName(),
          ProceedingChunk.class.getSimpleName(),
          User.class.getSimpleName(),
          UserTopicSubscription.class.getSimpleName(),
          IngestionLog.class.getSimpleName());

  @Container
  static PostgreSQLContainer<?> postgres =
      new PostgreSQLContainer<>("pgvector/pgvector:pg16")
          .withDatabaseName("bunge_summary")
          .withUsername("bunge")
          .withPassword("bunge");

  @DynamicPropertySource
  static void datasourceProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);
  }

  @Autowired private EntityManager entityManager;

  @Test
  void contextLoadsWithValidateAgainstFlywaySchema() {
    Set<String> managed =
        entityManager.getMetamodel().getEntities().stream()
            .map(EntityType::getName)
            .collect(Collectors.toSet());

    assertThat(managed).containsAll(REQUIRED_ENTITIES);
  }
}
