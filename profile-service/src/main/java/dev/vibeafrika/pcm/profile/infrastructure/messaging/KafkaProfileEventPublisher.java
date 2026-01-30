package dev.vibeafrika.pcm.profile.infrastructure.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.vibeafrika.pcm.profile.domain.event.ProfileCreatedEvent;
import dev.vibeafrika.pcm.profile.domain.event.ProfileUpdatedEvent;
import dev.vibeafrika.pcm.profile.domain.event.ProfileErasedEvent;
import dev.vibeafrika.pcm.profile.domain.event.ProfileEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Kafka implementation of the ProfileEventPublisher.
 * Translates domain events to Avro-generated event classes and publishes them.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaProfileEventPublisher implements ProfileEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${pcm.topics.profile-events:profile-events}")
    private String profileEventsTopic;

    @Override
    public void publish(ProfileCreatedEvent domainEvent) {
        dev.vibeafrika.pcm.events.ProfileCreatedEvent avroEvent = dev.vibeafrika.pcm.events.ProfileCreatedEvent
                .newBuilder()
                .setEventId(domainEvent.getEventId())
                .setEventType(domainEvent.getEventType())
                .setOccurredAt(domainEvent.getOccurredAt().toEpochMilli())
                .setVersion(1)
                .setTenantId(domainEvent.getTenantId())
                .setProfileId(domainEvent.getAggregateId())
                .setHandle(domainEvent.getHandle())
                .setAttributes(toJson(domainEvent.getAttributes()))
                .build();

        publish(avroEvent.getProfileId(), avroEvent);
    }

    @Override
    public void publish(ProfileUpdatedEvent domainEvent) {
        dev.vibeafrika.pcm.events.ProfileUpdatedEvent avroEvent = dev.vibeafrika.pcm.events.ProfileUpdatedEvent
                .newBuilder()
                .setEventId(domainEvent.getEventId())
                .setEventType(domainEvent.getEventType())
                .setOccurredAt(domainEvent.getOccurredAt().toEpochMilli())
                .setVersion(1)
                .setTenantId(domainEvent.getTenantId())
                .setProfileId(domainEvent.getAggregateId())
                .setUpdatedAttributes(toJson(domainEvent.getUpdatedAttributes()))
                .build();

        publish(avroEvent.getProfileId(), avroEvent);
    }

    @Override
    public void publish(ProfileErasedEvent domainEvent) {
        dev.vibeafrika.pcm.events.ProfileErasedEvent avroEvent = dev.vibeafrika.pcm.events.ProfileErasedEvent
                .newBuilder()
                .setEventId(domainEvent.getEventId())
                .setEventType(domainEvent.getEventType())
                .setOccurredAt(domainEvent.getOccurredAt().toEpochMilli())
                .setVersion(1)
                .setTenantId(domainEvent.getTenantId())
                .setProfileId(domainEvent.getAggregateId())
                .build();

        publish(avroEvent.getProfileId(), avroEvent);
    }

    private void publish(String key, Object event) {
        kafkaTemplate.send(profileEventsTopic, key, event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.debug("Published event to topic {}: {}", profileEventsTopic, event);
                    } else {
                        log.error("Failed to publish event to topic {}: {}", profileEventsTopic, ex.getMessage());
                    }
                });
    }

    private String toJson(Map<String, Object> map) {
        if (map == null)
            return null;
        try {
            return objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            log.error("Error serializing attributes to JSON", e);
            return null;
        }
    }
}
