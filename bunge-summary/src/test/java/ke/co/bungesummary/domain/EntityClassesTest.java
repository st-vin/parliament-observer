package ke.co.bungesummary.domain;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.Entity;
import java.nio.file.Files;
import java.nio.file.Path;
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

class EntityClassesTest {

  @Test
  void allNineCoreEntityClassesExist() throws Exception {
    Path entityDir =
        Path.of("src/main/java/ke/co/bungesummary/domain/entity");
    assertThat(Files.exists(entityDir)).isTrue();

    String[] required =
        {
          "Sitting.java",
          "Proceeding.java",
          "Topic.java",
          "Member.java",
          "Contribution.java",
          "ProceedingChunk.java",
          "User.java",
          "UserTopicSubscription.java",
          "IngestionLog.java"
        };

    for (String file : required) {
      assertThat(Files.exists(entityDir.resolve(file)))
          .as("entity file %s", file)
          .isTrue();
    }
  }

  @Test
  void coreEntitiesAreAnnotatedWithEntity() throws Exception {
    assertThat(Sitting.class.isAnnotationPresent(Entity.class)).isTrue();
    assertThat(Proceeding.class.isAnnotationPresent(Entity.class)).isTrue();
    assertThat(Topic.class.isAnnotationPresent(Entity.class)).isTrue();
    assertThat(Member.class.isAnnotationPresent(Entity.class)).isTrue();
    assertThat(Contribution.class.isAnnotationPresent(Entity.class)).isTrue();
    assertThat(ProceedingChunk.class.isAnnotationPresent(Entity.class)).isTrue();
    assertThat(User.class.isAnnotationPresent(Entity.class)).isTrue();
    assertThat(UserTopicSubscription.class.isAnnotationPresent(Entity.class)).isTrue();
    assertThat(IngestionLog.class.isAnnotationPresent(Entity.class)).isTrue();
  }
}
