package dev.vibeafrika.pcm.profile.application.usecase;

import dev.vibeafrika.pcm.common.usecase.UseCase;
import dev.vibeafrika.pcm.profile.domain.event.ProfileErasedEvent;
import dev.vibeafrika.pcm.profile.domain.event.ProfileEventPublisher;
import dev.vibeafrika.pcm.profile.domain.model.Profile;
import dev.vibeafrika.pcm.profile.domain.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Use case to erase a profile (GDPR right to be forgotten).
 */
@Service
@RequiredArgsConstructor
public class EraseProfileUseCase implements UseCase<EraseProfileUseCase.Input, Void> {

    private final ProfileRepository profileRepository;
    private final ProfileEventPublisher eventPublisher;

    @Override
    @Transactional
    @CacheEvict(value = "profiles", key = "#input.tenantId + ':' + #input.id")
    public Void execute(Input input) {
        Profile profile = profileRepository.findByIdAndTenantId(input.id, input.tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Profile not found: " + input.id));

        profile.erase();
        Profile savedProfile = profileRepository.save(profile);

        eventPublisher.publish(ProfileErasedEvent.of(
                savedProfile.getId().toString(),
                savedProfile.getTenantId()));

        return null;
    }

    public record Input(String tenantId, UUID id) {
    }
}
