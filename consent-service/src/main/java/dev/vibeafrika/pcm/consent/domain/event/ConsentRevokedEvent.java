package dev.vibeafrika.pcm.consent.domain.event;

import dev.vibeafrika.pcm.common.domain.DomainEvent;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain event emitted when consent is revoked.
 */
@Getter
@Builder
public class ConsentRevokedEvent implements DomainEvent {
    
    private final String eventId;
    private final String eventType;
    private final Instant occurredAt;
    private final String aggregateId;
    private final String tenantId;
    private final String profileId;
    private final String purpose;

    public static ConsentRevokedEvent of(String tenantId, String consentId, String profileId, String purpose) {
        return ConsentRevokedEvent.builder()
            .eventId(UUID.randomUUID().toString())
            .eventType("ConsentRevoked")
            .occurredAt(Instant.now())
            .aggregateId(consentId)
            .tenantId(tenantId)
            .profileId(profileId)
            .purpose(purpose)
            .build();
    }
}
