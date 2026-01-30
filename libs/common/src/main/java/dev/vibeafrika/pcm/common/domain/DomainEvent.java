package dev.vibeafrika.pcm.common.domain;

import java.time.Instant;

/**
 * Base interface for all domain events in PCM.
 * Domain events represent something that happened in the domain that domain experts care about.
 */
public interface DomainEvent {
    
    /**
     * Unique identifier for this event instance.
     * @return Event ID
     */
    String getEventId();
    
    /**
     * Type of the event (e.g., "ProfileCreated", "ConsentGranted").
     * @return Event type
     */
    String getEventType();
    
    /**
     * Timestamp when the event occurred.
     * @return Event timestamp
     */
    Instant getOccurredAt();
    
    /**
     * Aggregate ID that this event relates to.
     * @return Aggregate ID
     */
    String getAggregateId();
    
    /**
     * Tenant ID for multi-tenancy support.
     * @return Tenant ID
     */
    String getTenantId();
    
    /**
     * Version of the event schema for evolution support.
     * @return Schema version
     */
    default int getVersion() {
        return 1;
    }
}
