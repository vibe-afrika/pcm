package dev.vibeafrika.pcm.preference.application.usecase;

import dev.vibeafrika.pcm.common.usecase.UseCase;
import dev.vibeafrika.pcm.preference.domain.event.PreferenceEventPublisher;
import dev.vibeafrika.pcm.preference.domain.event.PreferenceUpdatedEvent;
import dev.vibeafrika.pcm.preference.domain.model.Preference;
import dev.vibeafrika.pcm.preference.domain.repository.PreferenceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

/**
 * Use case to update user preferences.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UpdatePreferenceUseCase implements UseCase<UpdatePreferenceUseCase.Input, Void> {

    private final PreferenceRepository preferenceRepository;
    private final PreferenceEventPublisher eventPublisher;

    @Override
    public Void execute(Input input) {
        log.info("Updating preferences for profile {}/{}", input.tenantId, input.profileId);

        Preference preference = preferenceRepository.findByProfileIdAndTenantId(input.profileId, input.tenantId)
            .orElseGet(() -> Preference.create(input.tenantId, input.profileId));

        preference.updateSettings(input.settings);
        
        Preference savedPreference = preferenceRepository.save(preference);

        eventPublisher.publish(PreferenceUpdatedEvent.of(
            savedPreference.getTenantId(),
            savedPreference.getId().toString(),
            savedPreference.getProfileId().toString(),
            savedPreference.getSettings()
        ));

        return null;
    }

    public record Input(String tenantId, UUID profileId, Map<String, String> settings) {}
}
