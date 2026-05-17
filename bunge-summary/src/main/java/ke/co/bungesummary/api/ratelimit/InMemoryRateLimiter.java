package ke.co.bungesummary.api.ratelimit;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class InMemoryRateLimiter {

    private final int maxRequests;
    private final long windowMillis;
    private final ConcurrentHashMap<String, Window> windows = new ConcurrentHashMap<>();

    public InMemoryRateLimiter(int maxRequests, long windowMillis) {
        this.maxRequests = maxRequests;
        this.windowMillis = windowMillis;
    }

    public boolean tryAcquire(String key) {
        long now = System.currentTimeMillis();
        Window window =
                windows.compute(
                        key,
                        (k, existing) -> {
                            if (existing == null || now - existing.startMillis >= windowMillis) {
                                return new Window(now);
                            }
                            return existing;
                        });
        return window.counter.incrementAndGet() <= maxRequests;
    }

    public long retryAfterSeconds(String key) {
        Window window = windows.get(key);
        if (window == null) {
            return windowMillis / 1000;
        }
        long elapsed = System.currentTimeMillis() - window.startMillis;
        long remaining = Math.max(1, (windowMillis - elapsed) / 1000);
        return remaining;
    }

    private static final class Window {
        private final long startMillis;
        private final AtomicInteger counter = new AtomicInteger(0);

        private Window(long startMillis) {
            this.startMillis = startMillis;
        }
    }
}
