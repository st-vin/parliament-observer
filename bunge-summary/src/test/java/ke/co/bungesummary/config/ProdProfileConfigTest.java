package ke.co.bungesummary.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(
        properties =
                "spring.autoconfigure.exclude="
                        + "org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,"
                        + "org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration,"
                        + "org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration,"
                        + "org.springframework.boot.autoconfigure.batch.BatchAutoConfiguration,"
                        + "org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration")
@ActiveProfiles("prod")
@TestPropertySource(
        properties = {
            "SPRING_DATASOURCE_URL=jdbc:postgresql://prod-host:5432/bunge_summary",
            "DB_USERNAME=prod_user",
            "DB_PASSWORD=prod_pass",
            // Prod profile group normally pulls in `api`; this test only validates env binding.
            "spring.profiles.group.prod="
        })
class ProdProfileConfigTest {

    @Autowired private Environment environment;

    @Test
    void prodProfileReadsDatasourceFromEnvironment() {
        assertThat(environment.getProperty("spring.datasource.url"))
                .isEqualTo("jdbc:postgresql://prod-host:5432/bunge_summary");
        assertThat(environment.getProperty("spring.datasource.username")).isEqualTo("prod_user");
        assertThat(environment.getProperty("spring.jpa.hibernate.ddl-auto")).isEqualTo("validate");
    }
}
