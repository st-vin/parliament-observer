package ke.co.bungesummary.config;

import jakarta.servlet.http.HttpServletResponse;
import ke.co.bungesummary.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@Profile("api")
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)
            throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(
                        auth ->
                                auth.requestMatchers("/api/v1/auth/**")
                                        .permitAll()
                                        .requestMatchers(HttpMethod.GET, "/api/v1/sittings/**")
                                        .permitAll()
                                        .requestMatchers(HttpMethod.GET, "/api/v1/proceedings/**")
                                        .permitAll()
                                        .requestMatchers(HttpMethod.GET, "/api/v1/topics/**")
                                        .permitAll()
                                        .requestMatchers(HttpMethod.GET, "/api/v1/members/**")
                                        .permitAll()
                                        .requestMatchers(HttpMethod.GET, "/api/v1/search/**")
                                        .permitAll()
                                        .requestMatchers(
                                                "/",
                                                "/browse/**",
                                                "/proceedings/**",
                                                "/login",
                                                "/register",
                                                "/logout",
                                                "/css/**",
                                                "/error",
                                                "/actuator/health")
                                        .permitAll()
                                        .requestMatchers("/profile", "/profile/**")
                                        .authenticated()
                                        .anyRequest()
                                        .authenticated())
                .exceptionHandling(
                        exceptions ->
                                exceptions.authenticationEntryPoint(
                                        (request, response, authException) -> {
                                            if (request.getRequestURI().startsWith("/api/")) {
                                                response.sendError(
                                                        HttpServletResponse.SC_UNAUTHORIZED,
                                                        "Unauthorized");
                                            } else {
                                                response.sendRedirect("/login");
                                            }
                                        }))
                .addFilterBefore(
                        jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
