package org.example.revshopapigateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.AbstractRateLimiter;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Primary
@Component("inMemoryRateLimiter")
public class InMemoryRateLimiter extends AbstractRateLimiter<InMemoryRateLimiter.Config> {

    private final Map<String, TokenBucket> tokenBuckets = new ConcurrentHashMap<>();

    public InMemoryRateLimiter() {
        super(Config.class, "in-memory-rate-limiter", null);
    }

    @Override
    public Mono<Response> isAllowed(String routeId, String id) {
        Config config = getConfig().getOrDefault(routeId, new Config(10, 20));
        TokenBucket tokenBucket = tokenBuckets.computeIfAbsent(id, k -> new TokenBucket(config.getReplenishRate(), config.getBurstCapacity()));

        int tokensLeft = tokenBucket.consumeToken();
        boolean isAllowed = tokensLeft >= 0;

        Response response = new Response(isAllowed, Map.of(
            "X-RateLimit-Remaining", String.valueOf(Math.max(0, tokensLeft)),
            "X-RateLimit-Burst-Capacity", String.valueOf(config.getBurstCapacity()),
            "X-RateLimit-Replenish-Rate", String.valueOf(config.getReplenishRate())
        ));

        return Mono.just(response);
    }

    public static class Config {
        private int replenishRate = 10;
        private int burstCapacity = 20;

        public Config() {}

        public Config(int replenishRate, int burstCapacity) {
            this.replenishRate = replenishRate;
            this.burstCapacity = burstCapacity;
        }

        public int getReplenishRate() { return replenishRate; }
        public void setReplenishRate(int replenishRate) { this.replenishRate = replenishRate; }
        public int getBurstCapacity() { return burstCapacity; }
        public void setBurstCapacity(int burstCapacity) { this.burstCapacity = burstCapacity; }
    }

    private static class TokenBucket {
        private final int replenishRate;
        private final int burstCapacity;
        private final AtomicInteger tokens;
        private long lastRefillTimestamp;

        public TokenBucket(int replenishRate, int burstCapacity) {
            this.replenishRate = replenishRate;
            this.burstCapacity = burstCapacity;
            this.tokens = new AtomicInteger(burstCapacity);
            this.lastRefillTimestamp = System.currentTimeMillis();
        }

        public synchronized int consumeToken() {
            refill();
            return tokens.decrementAndGet();
        }

        private void refill() {
            long now = System.currentTimeMillis();
            long elapsedTime = now - lastRefillTimestamp;
            int tokensToAdd = (int) (elapsedTime * replenishRate / 1000);

            if (tokensToAdd > 0) {
                tokens.set(Math.min(burstCapacity, tokens.get() + tokensToAdd));
                lastRefillTimestamp = now;
            }
        }
    }
}
