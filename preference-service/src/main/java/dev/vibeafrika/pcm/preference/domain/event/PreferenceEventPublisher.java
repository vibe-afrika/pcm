package dev.vibeafrika.pcm.preference.domain.event;

/**
 * Port for publishing preference-related events.
 */
public interface PreferenceEventPublisher {
    void publish(PreferenceUpdatedEvent event);
}
