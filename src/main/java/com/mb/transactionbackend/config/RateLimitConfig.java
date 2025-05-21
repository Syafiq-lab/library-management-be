package com.mb.transactionbackend.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@ConfigurationProperties(prefix = "ratelimit")
@Getter
@Setter
public class RateLimitConfig {

    private boolean enabled = true;
    private int requests = 100;
    private String duration = "1m";

    @Bean
    public Bucket tokenBucket() {
        if (!enabled) {
            return Bucket.builder().addLimit(Bandwidth.classic(Integer.MAX_VALUE, Refill.greedy(Integer.MAX_VALUE, Duration.ofNanos(1)))).build();
        }
        
        Duration parsedDuration = parseDuration(duration);
        Bandwidth limit = Bandwidth.classic(requests, Refill.greedy(requests, parsedDuration));
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }
    
    private Duration parseDuration(String duration) {
        if (duration == null || duration.isEmpty()) {
            return Duration.ofMinutes(1);
        }
        
        String value = duration.substring(0, duration.length() - 1);
        char unit = duration.charAt(duration.length() - 1);
        
        try {
            int amount = Integer.parseInt(value);
			return switch (unit) {
				case 's' -> Duration.ofSeconds(amount);
				case 'm' -> Duration.ofMinutes(amount);
				case 'h' -> Duration.ofHours(amount);
				case 'd' -> Duration.ofDays(amount);
				default -> Duration.ofMinutes(1);
			};
        } catch (NumberFormatException e) {
            return Duration.ofMinutes(1);
        }
    }
}