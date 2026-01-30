package dev.vibeafrika.pcm.profile.domain.event;

import dev.vibeafrika.pcm.common.domain.DomainEvent;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * Domain event emitted when a profile is updated.
 */
@Getter
@Builder
public class ProfileUpdatedEvent implements DomainEvent {
    
    private final String eventId;
    private final String eventType;
    private final Instant occurredAt;
    private final String aggregateId;
    private final String tenantId;
    private final Map<String, Object> updatedAttributes;
    
    public static ProfileUpdatedEvent of(String profileId, String tenantId, Map<String, Object> updatedAttributes) {
        return ProfileUpdatedEvent.builder()
            .eventId(UUID.randomUUID().toString())
            .eventType("ProfileUpdated")
            .occurredAt(Instant.now())
            .aggregateId(profileId)
            .tenantId(tenantId)
            .updatedAttributes(updatedAttributes)
            .build();
    }
}
