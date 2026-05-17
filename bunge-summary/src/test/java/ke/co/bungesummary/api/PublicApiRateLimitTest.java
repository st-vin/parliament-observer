package ke.co.bungesummary.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(properties = "spring.profiles.active=api-it")
@AutoConfigureMockMvc
@TestPropertySource(
    properties = {
      "api.rate-limit.requests-per-minute=2",
      "spring.datasource.url=jdbc:h2:mem:api_rate_test;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1"
    })
class PublicApiRateLimitTest {

  @Autowired private MockMvc mockMvc;

  @Test
  void exceedingRateLimitReturns429WithRetryAfter() throws Exception {
    mockMvc.perform(get("/api/v1/sittings")).andExpect(status().isOk());
    mockMvc.perform(get("/api/v1/sittings")).andExpect(status().isOk());
    mockMvc.perform(get("/api/v1/sittings"))
        .andExpect(status().isTooManyRequests())
        .andExpect(header().exists("Retry-After"));
  }
}
