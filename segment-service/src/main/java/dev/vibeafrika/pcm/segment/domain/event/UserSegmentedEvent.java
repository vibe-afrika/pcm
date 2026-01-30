package dev.vibeafrika.pcm.segment.domain.event;

import dev.vibeafrika.pcm.common.domain.DomainEvent;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Domain event emitted when a user's classification (segments/scores) changes.
 */
@Getter
@Builder
public class UserSegmentedEvent implements DomainEvent {
    
    private final String eventId;
    private final String eventType;
    private final Instant occurredAt;
    private final String aggregateId;
    private final String tenantId;
    private final String profileId;
    private final Set<String> tags;
    private final Map<String, Double> scores;

    public static UserSegmentedEvent of(String tenantId, String segmentId, String profileId, Set<String> tags, Map<String, Double> scores) {
        return UserSegmentedEvent.builder()
            .eventId(UUID.randomUUID().toString())
            .eventType("UserSegmented")
            .occurredAt(Instant.now())
            .aggregateId(segmentId)
            .tenantId(tenantId)
            .profileId(profileId)
            .tags(tags)
            .scores(scores)
            .build();
    }
}
