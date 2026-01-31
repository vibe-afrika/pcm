package dev.vibeafrika.pcm.profile.infrastructure.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.vibeafrika.pcm.profile.domain.event.ProfileCreatedEvent;
import dev.vibeafrika.pcm.profile.domain.event.ProfileUpdatedEvent;
import dev.vibeafrika.pcm.profile.domain.event.ProfileErasedEvent;
import dev.vibeafrika.pcm.profile.domain.event.ProfileEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
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

    private final StreamBridge streamBridge;
    private final ObjectMapper objectMapper;

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

        send("profileCreated-out-0", avroEvent.getProfileId(), avroEvent);
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

        send("profileUpdated-out-0", avroEvent.getProfileId(), avroEvent);
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

        send("profileErased-out-0", avroEvent.getProfileId(), avroEvent);
    }

    private void send(String bindingName, String key, Object event) {
        log.debug("Sending event to binding {}: {}", bindingName, event);
        boolean sent = streamBridge.send(bindingName,
                org.springframework.messaging.support.MessageBuilder
                        .withPayload(event)
                        .setHeader("partitionKey", key)
                        .build());

        if (sent) {
            log.debug("Successfully sent event to binding {}", bindingName);
        } else {
            log.error("Failed to send event to binding {}", bindingName);
        }
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
