package dev.vibeafrika.pcm.preference.domain.model;

import dev.vibeafrika.pcm.common.domain.AggregateRoot;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Preference aggregate root - represents user UX settings.
 * Stored in Redis for ultra-low latency access.
 */
@RedisHash("user_preferences")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Preference implements AggregateRoot<UUID> {

    @Id
    private UUID id;

    @Indexed
    private String tenantId;

    @Indexed
    private UUID profileId;

    private Map<String, String> settings = new HashMap<>();

    private Instant lastUpdated;

    private Preference(String tenantId, UUID profileId) {
        this.id = UUID.randomUUID();
        this.tenantId = tenantId;
        this.profileId = profileId;
        this.lastUpdated = Instant.now();
    }

    public static Preference create(String tenantId, UUID profileId) {
        return new Preference(tenantId, profileId);
    }

    public void updateSettings(Map<String, String> newSettings) {
        this.settings.putAll(newSettings);
        this.lastUpdated = Instant.now();
    }

    public void setSetting(String key, String value) {
        this.settings.put(key, value);
        this.lastUpdated = Instant.now();
    }

    public String getSetting(String key) {
        return settings.get(key);
    }
}
