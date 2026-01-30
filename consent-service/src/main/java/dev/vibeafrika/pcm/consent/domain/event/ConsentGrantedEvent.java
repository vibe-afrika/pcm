package dev.vibeafrika.pcm.consent.domain.event;

import dev.vibeafrika.pcm.common.domain.DomainEvent;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain event emitted when consent is granted.
 */
@Getter
@Builder
public class ConsentGrantedEvent implements DomainEvent {
    
    private final String eventId;
    private final String eventType;
    private final Instant occurredAt;
    private final String aggregateId;
    private final String tenantId;
    private final String profileId;
    private final String purpose;
    private final String consentVersion;
    private final String proofHash;

    public static ConsentGrantedEvent of(String tenantId, String consentId, String profileId, String purpose, String consentVersion, String proofHash) {
        return ConsentGrantedEvent.builder()
            .eventId(UUID.randomUUID().toString())
            .eventType("ConsentGranted")
            .occurredAt(Instant.now())
            .aggregateId(consentId)
            .tenantId(tenantId)
            .profileId(profileId)
            .purpose(purpose)
            .consentVersion(consentVersion)
            .proofHash(proofHash)
            .build();
    }
}
