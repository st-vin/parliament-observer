package ke.co.bungesummary.web;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import jakarta.servlet.http.Cookie;
import java.util.UUID;
import ke.co.bungesummary.security.AuthCookies;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@SpringBootTest(properties = "spring.profiles.active=web-it")
@AutoConfigureMockMvc
@ActiveProfiles("web-it")
class WebIntegrationTest {

    private static final UUID PROCEEDING_ID = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaa201");

    @Autowired private MockMvc mockMvc;

    @Test
    void homeShowsRecentProceedings() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(
                        org.springframework.test.web.servlet.result.MockMvcResultMatchers.content()
                                .string(containsString("Health Budget Allocation")));
    }

    @Test
    void browseDateShowsProceedingsForSittingDay() throws Exception {
        mockMvc.perform(get("/browse/date").param("year", "2024").param("month", "3").param("date", "2024-03-15"))
                .andExpect(status().isOk())
                .andExpect(view().name("browse-date"))
                .andExpect(
                        org.springframework.test.web.servlet.result.MockMvcResultMatchers.content()
                                .string(containsString("Health Budget Allocation")));
    }

    @Test
    void browseTopicListsTopicsWithCounts() throws Exception {
        mockMvc.perform(get("/browse/topic"))
                .andExpect(status().isOk())
                .andExpect(
                        org.springframework.test.web.servlet.result.MockMvcResultMatchers.content()
                                .string(containsString("Health")));
    }

    @Test
    void proceedingDetailShowsAiDisclaimerAndStance() throws Exception {
        mockMvc.perform(get("/proceedings/{id}", PROCEEDING_ID))
                .andExpect(status().isOk())
                .andExpect(
                        org.springframework.test.web.servlet.result.MockMvcResultMatchers.content()
                                .string(containsString("AI-generated")))
                .andExpect(
                        org.springframework.test.web.servlet.result.MockMvcResultMatchers.content()
                                .string(containsString("FOR")));
    }

    @Test
    void loginSetsJwtCookieAndAllowsProfile() throws Exception {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("email", "webuser@example.com");
        form.add("password", "password12345");

        mockMvc.perform(
                        post("/register")
                                .params(form)
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is3xxRedirection());

        MvcResult login =
                mockMvc.perform(
                                post("/login")
                                        .params(form)
                                        .with(csrf())
                                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                        .andExpect(status().is3xxRedirection())
                        .andExpect(redirectedUrl("/"))
                        .andExpect(cookie().exists(AuthCookies.JWT_COOKIE_NAME))
                        .andReturn();

        Cookie jwt =
                login.getResponse().getCookie(AuthCookies.JWT_COOKIE_NAME);

        mockMvc.perform(get("/profile").cookie(jwt))
                .andExpect(status().isOk())
                .andExpect(view().name("profile"));
    }

    @Test
    void trackTopicFromProfile() throws Exception {
        registerAndLogin("tracker@example.com");

        MvcResult login =
                mockMvc.perform(
                                post("/login")
                                        .param("email", "tracker@example.com")
                                        .param("password", "password12345")
                                        .with(csrf()))
                        .andExpect(status().is3xxRedirection())
                        .andReturn();

        Cookie jwt = login.getResponse().getCookie(AuthCookies.JWT_COOKIE_NAME);

        mockMvc.perform(
                        post("/profile/topics/health/track")
                                .param("returnTo", "/profile")
                                .cookie(jwt)
                                .with(csrf()))
                .andExpect(status().is3xxRedirection());

        mockMvc.perform(get("/profile").cookie(jwt))
                .andExpect(status().isOk())
                .andExpect(
                        org.springframework.test.web.servlet.result.MockMvcResultMatchers.content()
                                .string(containsString("Health")));
    }

    private void registerAndLogin(String email) throws Exception {
        mockMvc.perform(
                        post("/register")
                                .param("email", email)
                                .param("password", "password12345")
                                .with(csrf()))
                .andExpect(status().is3xxRedirection());
    }
}
