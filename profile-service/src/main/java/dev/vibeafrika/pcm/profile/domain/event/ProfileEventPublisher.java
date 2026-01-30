package dev.vibeafrika.pcm.profile.domain.event;

/**
 * Port interface for publishing profile-related domain events.
 */
public interface ProfileEventPublisher {

    void publish(ProfileCreatedEvent event);

    void publish(ProfileUpdatedEvent event);

    void publish(ProfileErasedEvent event);
}
