package dev.vibeafrika.pcm.preference.infrastructure.messaging;

import dev.vibeafrika.pcm.events.ProfileErasedEvent;
import dev.vibeafrika.pcm.preference.domain.repository.PreferenceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.function.Consumer;

/**
 * Consumer for profile erasure events using Spring Cloud Stream.
 * Ensures user preferences are deleted when a profile is erased (GDPR).
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaProfileEventConsumer {

    private final PreferenceRepository preferenceRepository;

    @Bean
    public Consumer<ProfileErasedEvent> profileErased() {
        return event -> {
            log.info("Received ProfileErasedEvent for profile: {}. Cleaning up preferences.", event.getProfileId());
            try {
                UUID profileId = UUID.fromString(event.getProfileId().toString());
                preferenceRepository.findByProfileId(profileId).ifPresent(preference -> {
                    log.info("Deleting preferences for profile: {}", profileId);
                    preferenceRepository.delete(preference);
                });
            } catch (Exception e) {
                log.error("Failed to process profile erasure for preferences: {}", e.getMessage());
            }
        };
    }
}
