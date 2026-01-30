package dev.vibeafrika.pcm.consent.domain.event;

/**
 * Port interface for publishing consent-related domain events.
 */
public interface ConsentEventPublisher {
    
    void publish(ConsentGrantedEvent event);
    
    void publish(ConsentRevokedEvent event);
}
