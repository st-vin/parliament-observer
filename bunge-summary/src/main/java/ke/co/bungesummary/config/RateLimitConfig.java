package ke.co.bungesummary.config;

import ke.co.bungesummary.api.ratelimit.InMemoryRateLimiter;
import ke.co.bungesummary.api.ratelimit.PublicApiRateLimitFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;

@Configuration
@Profile("api")
public class RateLimitConfig {

    @Bean
    InMemoryRateLimiter publicApiRateLimiter(
            @Value("${api.rate-limit.requests-per-minute:120}") int requestsPerMinute) {
        return new InMemoryRateLimiter(requestsPerMinute, 60_000L);
    }

    @Bean
    FilterRegistrationBean<PublicApiRateLimitFilter> publicApiRateLimitFilter(
            InMemoryRateLimiter publicApiRateLimiter) {
        FilterRegistrationBean<PublicApiRateLimitFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new PublicApiRateLimitFilter(publicApiRateLimiter));
        registration.addUrlPatterns("/api/v1/*");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registration;
    }
}
