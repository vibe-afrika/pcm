package dev.vibeafrika.pcm.segment.infrastructure.messaging;

import dev.vibeafrika.pcm.events.ProfileCreatedEvent;
import dev.vibeafrika.pcm.events.ProfileUpdatedEvent;
import dev.vibeafrika.pcm.segment.application.usecase.ClassifyUserUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;

import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Consumer for profile-related events using Spring Cloud Stream.
 * Triggers re-classification whenever a profile is created or updated.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaProfileEventConsumer {

    private final ClassifyUserUseCase classifyUserUseCase;
    private final com.fasterxml.jackson.databind.ObjectMapper objectMapper;

    @Bean
    public Consumer<ProfileCreatedEvent> profileCreated() {
        return event -> {
            log.info("Received ProfileCreatedEvent for profile: {}", event.getProfileId());
            try {
                Map<String, Object> attributes = objectMapper.readValue(event.getAttributes().toString(),
                        new TypeReference<Map<String, Object>>() {
                        });

                classifyUserUseCase.execute(new ClassifyUserUseCase.Input(
                        event.getTenantId().toString(),
                        UUID.fromString(event.getProfileId().toString()),
                        attributes));
            } catch (Exception e) {
                log.error("Failed to parse profile attributes for classification: {}", e.getMessage());
            }
        };
    }

    @Bean
    public Consumer<ProfileUpdatedEvent> profileUpdated() {
        return event -> {
            log.info("Received ProfileUpdatedEvent for profile: {}", event.getProfileId());
            try {
                Map<String, Object> attributes = objectMapper.readValue(event.getUpdatedAttributes().toString(),
                        new TypeReference<Map<String, Object>>() {
                        });

                classifyUserUseCase.execute(new ClassifyUserUseCase.Input(
                        event.getTenantId().toString(),
                        UUID.fromString(event.getProfileId().toString()),
                        attributes));
            } catch (Exception e) {
                log.error("Failed to parse updated profile attributes for classification: {}", e.getMessage());
            }
        };
    }
}
