package ke.co.bungesummary.api;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest(properties = "spring.profiles.active=api-it")
@AutoConfigureMockMvc
class ApiIntegrationTest {

  private static final UUID SITTING_ID = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaa101");
  private static final UUID PROCEEDING_ID = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaa201");
  private static final UUID MEMBER_ID = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaa401");
  private static final UUID UNKNOWN_ID = UUID.fromString("00000000-0000-0000-0000-000000000099");

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @Test
  void listSittingsReturnsPagedJson() throws Exception {
    mockMvc.perform(get("/api/v1/sittings"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(1))))
        .andExpect(jsonPath("$.content[?(@.date=='2024-03-15')].proceedingCount").value(1))
        .andExpect(jsonPath("$.page.number").value(0))
        .andExpect(jsonPath("$.page.totalElements").value(2));
  }

  @Test
  void getSittingByIdReturnsDetailWithProceedings() throws Exception {
    mockMvc.perform(get("/api/v1/sittings/{id}", SITTING_ID))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.proceedings", hasSize(1)))
        .andExpect(jsonPath("$.proceedings[0].title").value("Health Budget Allocation"));
  }

  @Test
  void getUnknownSittingReturns404() throws Exception {
    mockMvc.perform(get("/api/v1/sittings/{id}", UNKNOWN_ID)).andExpect(status().isNotFound());
  }

  @Test
  void listProceedingsFiltersBySittingId() throws Exception {
    mockMvc.perform(get("/api/v1/proceedings").param("sittingId", SITTING_ID.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content", hasSize(1)))
        .andExpect(jsonPath("$.content[0].title").value("Health Budget Allocation"));
  }

  @Test
  void getProceedingDetailIncludesSummaryTopicsAndContributions() throws Exception {
    mockMvc.perform(get("/api/v1/proceedings/{id}", PROCEEDING_ID))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.plainSummary").exists())
        .andExpect(jsonPath("$.topics[0].slug").value("health"))
        .andExpect(jsonPath("$.contributions[0].stance").value("FOR"))
        .andExpect(jsonPath("$.contributions[0].verbatimExcerpt").exists());
  }

  @Test
  void listTopicsIncludesProceedingCounts() throws Exception {
    mockMvc.perform(get("/api/v1/topics"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[?(@.slug=='health')].proceedingCount").value(1));
  }

  @Test
  void topicProceedingsEndpointReturnsFilteredList() throws Exception {
    mockMvc.perform(get("/api/v1/topics/health/proceedings"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content", hasSize(1)))
        .andExpect(jsonPath("$.content[0].topics[0].slug").value("health"));
  }

  @Test
  void memberContributionsIncludeStanceAndVerbatimExcerpt() throws Exception {
    mockMvc.perform(get("/api/v1/members/{id}/contributions", MEMBER_ID))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].stance").value("FOR"))
        .andExpect(jsonPath("$.content[0].verbatimExcerpt").exists());
  }

  @Test
  void trackingSubscribeCreatesSubscriptionWithJwt() throws Exception {
    String token = registerAndLogin();

    mockMvc.perform(
            post("/api/v1/tracking/topics")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(
                        Map.of("topicSlugs", java.util.List.of("health")))))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$[0].slug").value("health"));

    mockMvc.perform(
            get("/api/v1/tracking/topics").header("Authorization", "Bearer " + token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].slug").value("health"));
  }

  private String registerAndLogin() throws Exception {
    String email = "api-it-" + UUID.randomUUID() + "@example.com";
    String password = "password123";

    mockMvc.perform(
            post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(Map.of("email", email, "password", password))))
        .andExpect(status().isCreated());

    MvcResult loginResult =
        mockMvc.perform(
                post("/api/v1/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        objectMapper.writeValueAsString(
                            Map.of("email", email, "password", password))))
            .andExpect(status().isOk())
            .andReturn();

    return objectMapper
        .readTree(loginResult.getResponse().getContentAsString())
        .get("token")
        .asText();
  }
}
