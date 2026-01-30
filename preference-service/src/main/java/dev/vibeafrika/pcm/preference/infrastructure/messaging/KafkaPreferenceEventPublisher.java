package dev.vibeafrika.pcm.preference.infrastructure.messaging;

import dev.vibeafrika.pcm.events.PreferenceUpdatedEvent;
import dev.vibeafrika.pcm.preference.domain.event.PreferenceEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Kafka implementation of PreferenceEventPublisher.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaPreferenceEventPublisher implements PreferenceEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${pcm.topics.preference-events:preference-events}")
    private String preferenceEventsTopic;

    @Override
    public void publish(dev.vibeafrika.pcm.preference.domain.event.PreferenceUpdatedEvent domainEvent) {
        PreferenceUpdatedEvent avroEvent = PreferenceUpdatedEvent.newBuilder()
            .setEventId(domainEvent.getEventId())
            .setEventType(domainEvent.getEventType())
            .setOccurredAt(domainEvent.getOccurredAt().toEpochMilli())
            .setVersion(1)
            .setTenantId(domainEvent.getTenantId())
            .setProfileId(domainEvent.getProfileId())
            .setPreferences(domainEvent.getPreferences())
            .build();

        kafkaTemplate.send(preferenceEventsTopic, avroEvent.getProfileId().toString(), avroEvent)
            .whenComplete((result, ex) -> {
                if (ex == null) {
                    log.debug("Published preference event to topic {}: {}", preferenceEventsTopic, avroEvent);
                } else {
                    log.error("Failed to publish preference event to topic {}: {}", preferenceEventsTopic, ex.getMessage());
                }
            });
    }
}
