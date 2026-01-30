package dev.vibeafrika.pcm.segment.domain.event;

/**
 * Port for publishing segment-related events.
 */
public interface SegmentEventPublisher {
    void publish(UserSegmentedEvent event);
}
