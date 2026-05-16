package ke.co.bungesummary.api;

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

@SpringBootTest(properties = "spring.profiles.active=auth-it")
@AutoConfigureMockMvc
class AuthSecurityTest {

    @Autowired private MockMvc mockMvc;

    @Autowired private ObjectMapper objectMapper;

    @Test
    void registerReturns201WithUserId() throws Exception {
        String email = "user-" + UUID.randomUUID() + "@example.com";
        mockMvc.perform(
                        post("/api/v1/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(
                                                Map.of("email", email, "password", "password123"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.emailVerified").value(false));
    }

    @Test
    void loginReturnsJwtWhenCredentialsValid() throws Exception {
        String email = "login-" + UUID.randomUUID() + "@example.com";
        String password = "password123";

        mockMvc.perform(
                        post("/api/v1/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(
                                                Map.of("email", email, "password", password))))
                .andExpect(status().isCreated());

        mockMvc.perform(
                        post("/api/v1/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(
                                                Map.of("email", email, "password", password))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.email").value(email));
    }

    @Test
    void sittingsEndpointIsPublicWithoutToken() throws Exception {
        mockMvc.perform(get("/api/v1/sittings")).andExpect(status().isOk());
    }

    @Test
    void protectedEndpointRequiresToken() throws Exception {
        mockMvc.perform(post("/api/v1/tracking/topics")).andExpect(status().isUnauthorized());
    }

    @Test
    void protectedEndpointAcceptsValidJwt() throws Exception {
        String email = "protected-" + UUID.randomUUID() + "@example.com";
        String password = "password123";

        mockMvc.perform(
                        post("/api/v1/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(
                                                Map.of("email", email, "password", password))));

        MvcResult loginResult =
                mockMvc.perform(
                                post("/api/v1/auth/login")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(
                                                objectMapper.writeValueAsString(
                                                        Map.of(
                                                                "email", email,
                                                                "password", password))))
                        .andExpect(status().isOk())
                        .andReturn();

        String token =
                objectMapper
                        .readTree(loginResult.getResponse().getContentAsString())
                        .get("token")
                        .asText();

        mockMvc.perform(
                        post("/api/v1/tracking/topics")
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(Map.of("topicSlug", "health"))))
                .andExpect(status().isNotFound());
    }
}
