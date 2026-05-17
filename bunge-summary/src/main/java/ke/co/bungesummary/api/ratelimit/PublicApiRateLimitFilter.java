package ke.co.bungesummary.api.ratelimit;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.HttpMethod;
import org.springframework.web.filter.OncePerRequestFilter;

public class PublicApiRateLimitFilter extends OncePerRequestFilter {

    private final InMemoryRateLimiter rateLimiter;

    public PublicApiRateLimitFilter(InMemoryRateLimiter rateLimiter) {
        this.rateLimiter = rateLimiter;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        if (!HttpMethod.GET.matches(request.getMethod())) {
            return true;
        }
        String path = request.getRequestURI();
        return !path.startsWith("/api/v1/sittings")
                && !path.startsWith("/api/v1/proceedings")
                && !path.startsWith("/api/v1/topics")
                && !path.startsWith("/api/v1/members")
                && !path.startsWith("/api/v1/search");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String clientKey = resolveClientKey(request);
        if (!rateLimiter.tryAcquire(clientKey)) {
            response.setStatus(429);
            response.setHeader(
                    "Retry-After", String.valueOf(rateLimiter.retryAfterSeconds(clientKey)));
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Too many requests\"}");
            return;
        }
        filterChain.doFilter(request, response);
    }

    private static String resolveClientKey(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
