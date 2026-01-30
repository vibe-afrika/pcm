package dev.vibeafrika.pcm.profile.domain.event;

import dev.vibeafrika.pcm.common.domain.DomainEvent;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * Domain event emitted when a new profile is created.
 */
@Getter
@Builder
public class ProfileCreatedEvent implements DomainEvent {
    
    private final String eventId;
    private final String eventType;
    private final Instant occurredAt;
    private final String aggregateId;
    private final String tenantId;
    private final String handle;
    private final Map<String, Object> attributes;
    
    public static ProfileCreatedEvent of(String profileId, String tenantId, String handle, Map<String, Object> attributes) {
        return ProfileCreatedEvent.builder()
            .eventId(UUID.randomUUID().toString())
            .eventType("ProfileCreated")
            .occurredAt(Instant.now())
            .aggregateId(profileId)
            .tenantId(tenantId)
            .handle(handle)
            .attributes(attributes)
            .build();
    }
}
