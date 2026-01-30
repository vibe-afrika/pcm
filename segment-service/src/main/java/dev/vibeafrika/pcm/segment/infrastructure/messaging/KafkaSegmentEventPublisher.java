package dev.vibeafrika.pcm.segment.infrastructure.messaging;

import dev.vibeafrika.pcm.events.UserSegmentedEvent;
import dev.vibeafrika.pcm.segment.domain.event.SegmentEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

/**
 * Kafka implementation of SegmentEventPublisher.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaSegmentEventPublisher implements SegmentEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${pcm.topics.segment-events:segment-events}")
    private String segmentEventsTopic;

    @Override
    public void publish(dev.vibeafrika.pcm.segment.domain.event.UserSegmentedEvent domainEvent) {
        UserSegmentedEvent avroEvent = UserSegmentedEvent.newBuilder()
            .setEventId(domainEvent.getEventId())
            .setEventType(domainEvent.getEventType())
            .setOccurredAt(domainEvent.getOccurredAt().toEpochMilli())
            .setVersion(1)
            .setTenantId(domainEvent.getTenantId())
            .setProfileId(domainEvent.getProfileId())
            .setTags(new ArrayList<>(domainEvent.getTags()))
            .setScores(domainEvent.getScores())
            .build();

        kafkaTemplate.send(segmentEventsTopic, avroEvent.getProfileId().toString(), avroEvent)
            .whenComplete((result, ex) -> {
                if (ex == null) {
                    log.debug("Published segment event to topic {}: {}", segmentEventsTopic, avroEvent);
                } else {
                    log.error("Failed to publish segment event to topic {}: {}", segmentEventsTopic, ex.getMessage());
                }
            });
    }
}
