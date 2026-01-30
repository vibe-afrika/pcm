package dev.vibeafrika.pcm.consent.infrastructure.messaging;

import dev.vibeafrika.pcm.consent.domain.event.ConsentEventPublisher;
import dev.vibeafrika.pcm.consent.domain.event.ConsentGrantedEvent;
import dev.vibeafrika.pcm.consent.domain.event.ConsentRevokedEvent;
import dev.vibeafrika.pcm.events.ConsentPurpose;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Kafka implementation of ConsentEventPublisher.
 * Translates domain events to Avro-generated events and publishes them.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaConsentEventPublisher implements ConsentEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${pcm.topics.consent-events:consent-events}")
    private String consentEventsTopic;

    @Override
    public void publish(ConsentGrantedEvent domainEvent) {
        dev.vibeafrika.pcm.events.ConsentGrantedEvent avroEvent = dev.vibeafrika.pcm.events.ConsentGrantedEvent.newBuilder()
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

        publish(avroEvent.getProfileId().toString(), avroEvent);
    }

    @Override
    public void publish(ConsentRevokedEvent domainEvent) {
        dev.vibeafrika.pcm.events.ConsentRevokedEvent avroEvent = dev.vibeafrika.pcm.events.ConsentRevokedEvent.newBuilder()
            .setEventId(domainEvent.getEventId())
            .setEventType(domainEvent.getEventType())
            .setOccurredAt(domainEvent.getOccurredAt().toEpochMilli())
            .setVersion(1)
            .setTenantId(domainEvent.getTenantId())
            .setConsentId(domainEvent.getAggregateId())
            .setProfileId(domainEvent.getProfileId())
            .setPurpose(ConsentPurpose.valueOf(domainEvent.getPurpose()))
            .build();

        publish(avroEvent.getProfileId().toString(), avroEvent);
    }

    private void publish(String key, Object event) {
        kafkaTemplate.send(consentEventsTopic, key, event)
            .whenComplete((result, ex) -> {
                if (ex == null) {
                    log.debug("Published consent event to topic {}: {}", consentEventsTopic, event);
                } else {
                    log.error("Failed to publish consent event to topic {}: {}", consentEventsTopic, ex.getMessage());
                }
            });
    }
}
