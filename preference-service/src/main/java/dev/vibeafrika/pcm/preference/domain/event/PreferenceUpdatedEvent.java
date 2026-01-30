package dev.vibeafrika.pcm.preference.domain.event;

import dev.vibeafrika.pcm.common.domain.DomainEvent;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * Domain event emitted when user preferences are updated.
 */
@Getter
@Builder
public class PreferenceUpdatedEvent implements DomainEvent {
    
    private final String eventId;
    private final String eventType;
    private final Instant occurredAt;
    private final String aggregateId;
    private final String tenantId;
    private final String profileId;
    private final Map<String, String> preferences;

    public static PreferenceUpdatedEvent of(String tenantId, String preferenceId, String profileId, Map<String, String> preferences) {
        return PreferenceUpdatedEvent.builder()
            .eventId(UUID.randomUUID().toString())
            .eventType("PreferenceUpdated")
            .occurredAt(Instant.now())
            .aggregateId(preferenceId)
            .tenantId(tenantId)
            .profileId(profileId)
            .preferences(preferences)
            .build();
    }
}
