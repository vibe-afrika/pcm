package dev.vibeafrika.pcm.segment.infrastructure.messaging;

import dev.vibeafrika.pcm.events.ProfileCreatedEvent;
import dev.vibeafrika.pcm.events.ProfileUpdatedEvent;
import dev.vibeafrika.pcm.segment.application.usecase.ClassifyUserUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

/**
 * Kafka consumer for profile-related events.
 * Triggers re-classification whenever a profile is created or updated.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaProfileEventConsumer {

    private final ClassifyUserUseCase classifyUserUseCase;
    private final com.fasterxml.jackson.databind.ObjectMapper objectMapper;

    @KafkaListener(topics = "${pcm.topics.profile-events:profile-events}")
    public void handleProfileCreated(ProfileCreatedEvent event) {
        log.info("Received ProfileCreatedEvent for profile: {}", event.getProfileId());
        try {
            Map<String, Object> attributes = objectMapper.readValue(event.getAttributes().toString(), 
                new com.fasterxml.jackson.core.type.TypeReference<Map<String, Object>>() {});
            
            classifyUserUseCase.execute(new ClassifyUserUseCase.Input(
                event.getTenantId().toString(),
                UUID.fromString(event.getProfileId().toString()),
                attributes
            ));
        } catch (Exception e) {
            log.error("Failed to parse profile attributes for classification: {}", e.getMessage());
        }
    }

    @KafkaListener(topics = "${pcm.topics.profile-events:profile-events}")
    public void handleProfileUpdated(ProfileUpdatedEvent event) {
        log.info("Received ProfileUpdatedEvent for profile: {}", event.getProfileId());
        try {
            Map<String, Object> attributes = objectMapper.readValue(event.getUpdatedAttributes().toString(), 
                new com.fasterxml.jackson.core.type.TypeReference<Map<String, Object>>() {});

            classifyUserUseCase.execute(new ClassifyUserUseCase.Input(
                event.getTenantId().toString(),
                UUID.fromString(event.getProfileId().toString()),
                attributes
            ));
        } catch (Exception e) {
            log.error("Failed to parse updated profile attributes for classification: {}", e.getMessage());
        }
    }
}
