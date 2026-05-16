package ke.co.bungesummary.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(
        properties =
                "spring.autoconfigure.exclude="
                        + "org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,"
                        + "org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration,"
                        + "org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration,"
                        + "org.springframework.boot.autoconfigure.batch.BatchAutoConfiguration,"
                        + "org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration")
@ActiveProfiles("config-check")
class ApplicationConfigTest {

    @Autowired private Environment environment;

    @Test
    void configCheckProfileUsesDockerComposePostgresUrl() {
        assertThat(environment.getProperty("spring.datasource.url"))
                .isEqualTo("jdbc:postgresql://localhost:5433/bunge_summary");
        assertThat(environment.getProperty("spring.datasource.username")).isEqualTo("bunge");
        assertThat(environment.getProperty("spring.datasource.password")).isEqualTo("bunge");
    }

    @Test
    void devProfileExposesGeminiResendAndJwtKeys() {
        assertThat(environment.getProperty("gemini.api-key")).isNotNull();
        assertThat(environment.getProperty("gemini.model")).isEqualTo("gemini-1.5-flash");
        assertThat(environment.getProperty("gemini.embedding-model"))
                .isEqualTo("text-embedding-004");
        assertThat(environment.getProperty("jwt.secret")).isNotBlank();
        assertThat(environment.getProperty("notification.email.resend-api-key")).isNotNull();
        assertThat(environment.getProperty("openrouter.api-key")).isNotNull();
    }

    @Test
    void envExampleDocumentsRequiredSecrets() throws Exception {
        Path envExample = Path.of(".env.example");
        assertThat(Files.exists(envExample)).isTrue();

        String content = Files.readString(envExample);
        assertThat(content).contains("GEMINI_API_KEY");
        assertThat(content).contains("RESEND_API_KEY");
        assertThat(content).contains("JWT_SECRET");
    }
}
