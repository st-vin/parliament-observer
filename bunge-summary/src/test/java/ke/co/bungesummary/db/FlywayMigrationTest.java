package ke.co.bungesummary.db;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@ActiveProfiles("flyway-it")
@Testcontainers(disabledWithoutDocker = true)
class FlywayMigrationTest {

    private static final List<String> CORE_TABLES =
            List.of(
                    "sittings",
                    "proceedings",
                    "topics",
                    "proceeding_topics",
                    "members",
                    "contributions",
                    "users",
                    "user_topic_subscriptions",
                    "ingestion_log");

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

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void v1CreatesAllNineCoreTables() {
        for (String table : CORE_TABLES) {
            Integer count =
                    jdbcTemplate.queryForObject(
                            """
                            SELECT COUNT(*) FROM information_schema.tables
                            WHERE table_schema = 'public' AND table_name = ?
                            """,
                            Integer.class,
                            table);
            assertThat(count).as("table %s", table).isEqualTo(1);
        }
    }

    @Test
    void sittingsDateIsUnique() {
        Integer uniqueOnDate =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*) FROM pg_constraint c
                        JOIN pg_class t ON c.conrelid = t.oid
                        WHERE t.relname = 'sittings'
                          AND c.contype = 'u'
                          AND pg_get_constraintdef(c.oid) LIKE '%date%'
                        """,
                        Integer.class);
        assertThat(uniqueOnDate).isGreaterThanOrEqualTo(1);
    }

    @Test
    void v2CreatesProceedingChunksWithVector768() {
        Integer tableExists =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*) FROM information_schema.tables
                        WHERE table_schema = 'public' AND table_name = 'proceeding_chunks'
                        """,
                        Integer.class);
        assertThat(tableExists).isEqualTo(1);

        String udtName =
                jdbcTemplate.queryForObject(
                        """
                        SELECT udt_name
                        FROM information_schema.columns
                        WHERE table_schema = 'public'
                          AND table_name = 'proceeding_chunks'
                          AND column_name = 'embedding'
                        """,
                        String.class);
        assertThat(udtName).isEqualTo("vector");

        String typeModifier =
                jdbcTemplate.queryForObject(
                        """
                        SELECT format_type(a.atttypid, a.atttypmod)
                        FROM pg_attribute a
                        JOIN pg_class c ON a.attrelid = c.oid
                        WHERE c.relname = 'proceeding_chunks'
                          AND a.attname = 'embedding'
                          AND NOT a.attisdropped
                        """,
                        String.class);
        assertThat(typeModifier).isEqualTo("vector(768)");
    }

    @Test
    void v2CreatesIvfflatIndexOnEmbedding() {
        Integer indexCount =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*) FROM pg_indexes
                        WHERE schemaname = 'public'
                          AND tablename = 'proceeding_chunks'
                          AND indexdef LIKE '%ivfflat%'
                        """,
                        Integer.class);
        assertThat(indexCount).isGreaterThanOrEqualTo(1);
    }

    @Test
    void v3SeedsAtLeastTenTopics() {
        Integer topicCount =
                jdbcTemplate.queryForObject("SELECT COUNT(*) FROM topics", Integer.class);
        assertThat(topicCount).isGreaterThanOrEqualTo(10);
    }
}
