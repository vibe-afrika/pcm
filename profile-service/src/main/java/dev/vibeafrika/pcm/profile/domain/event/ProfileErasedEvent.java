package dev.vibeafrika.pcm.profile.domain.event;

import dev.vibeafrika.pcm.common.domain.DomainEvent;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain event emitted when a profile is erased.
 */
@Getter
@Builder
public class ProfileErasedEvent implements DomainEvent {

    private final String eventId;
    private final String eventType;
    private final Instant occurredAt;
    private final String aggregateId;
    private final String tenantId;

    public static ProfileErasedEvent of(String profileId, String tenantId) {
        return ProfileErasedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType("ProfileErased")
                .occurredAt(Instant.now())
                .aggregateId(profileId)
                .tenantId(tenantId)
                .build();
    }
}
