package dev.vibeafrika.pcm.consent.infrastructure.messaging;

import dev.vibeafrika.pcm.consent.domain.event.ConsentEventPublisher;
import dev.vibeafrika.pcm.consent.domain.event.ConsentGrantedEvent;
import dev.vibeafrika.pcm.consent.domain.event.ConsentRevokedEvent;
import dev.vibeafrika.pcm.events.ConsentPurpose;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;

/**
 * Kafka implementation of ConsentEventPublisher.
 * Translates domain events to Avro-generated events and publishes them.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaConsentEventPublisher implements ConsentEventPublisher {

    private final StreamBridge streamBridge;

    @Override
    public void publish(ConsentGrantedEvent domainEvent) {
        dev.vibeafrika.pcm.events.ConsentGrantedEvent avroEvent = dev.vibeafrika.pcm.events.ConsentGrantedEvent
                .newBuilder()
                .setEventId(domainEvent.getEventId())
                .setEventType(domainEvent.getEventType())
                .setOccurredAt(domainEvent.getOccurredAt().toEpochMilli())
                .setVersion(1)
                .setTenantId(domainEvent.getTenantId())
                .setConsentId(domainEvent.getAggregateId())
                .setProfileId(domainEvent.getProfileId())
                .setPurpose(ConsentPurpose.valueOf(domainEvent.getPurpose()))
                .setConsentVersion(domainEvent.getConsentVersion())
                .setProofHash(domainEvent.getProofHash())
                .build();

        send("consentGranted-out-0", avroEvent.getProfileId().toString(), avroEvent);
    }

    @Override
    public void publish(ConsentRevokedEvent domainEvent) {
        dev.vibeafrika.pcm.events.ConsentRevokedEvent avroEvent = dev.vibeafrika.pcm.events.ConsentRevokedEvent
                .newBuilder()
                .setEventId(domainEvent.getEventId())
                .setEventType(domainEvent.getEventType())
                .setOccurredAt(domainEvent.getOccurredAt().toEpochMilli())
                .setVersion(1)
                .setTenantId(domainEvent.getTenantId())
                .setConsentId(domainEvent.getAggregateId())
                .setProfileId(domainEvent.getProfileId())
                .setPurpose(ConsentPurpose.valueOf(domainEvent.getPurpose()))
                .build();

        send("consentRevoked-out-0", avroEvent.getProfileId().toString(), avroEvent);
    }

    private void send(String bindingName, String key, Object event) {
        log.debug("Sending consent event to binding {}: {}", bindingName, event);
        streamBridge.send(bindingName,
                org.springframework.messaging.support.MessageBuilder
                        .withPayload(event)
                        .setHeader("partitionKey", key)
                        .build());
    }
}
