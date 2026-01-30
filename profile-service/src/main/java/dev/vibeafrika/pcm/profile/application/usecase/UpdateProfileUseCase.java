package dev.vibeafrika.pcm.profile.application.usecase;

import dev.vibeafrika.pcm.common.usecase.UseCase;
import dev.vibeafrika.pcm.profile.application.dto.ProfileResponse;
import dev.vibeafrika.pcm.profile.application.dto.UpdateProfileCommand;
import dev.vibeafrika.pcm.profile.application.service.PIIProtectionService;
import dev.vibeafrika.pcm.profile.domain.event.ProfileEventPublisher;
import dev.vibeafrika.pcm.profile.domain.event.ProfileUpdatedEvent;
import dev.vibeafrika.pcm.profile.domain.model.Profile;
import dev.vibeafrika.pcm.profile.domain.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Use case to update a profile's attributes.
 */
@Service
@RequiredArgsConstructor
public class UpdateProfileUseCase implements UseCase<UpdateProfileUseCase.Input, ProfileResponse> {

    private final ProfileRepository profileRepository;
    private final ProfileEventPublisher eventPublisher;
    private final PIIProtectionService piiProtectionService;

    @Override
    @Transactional
    @CacheEvict(value = "profiles", key = "#input.tenantId + ':' + #input.id")
    public ProfileResponse execute(Input input) {
        Profile profile = profileRepository.findByIdAndTenantId(input.id, input.tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Profile not found: " + input.id));

        // Protect PII fields before updating
        var protectedAttributes = piiProtectionService.protect(input.command.getAttributes());

        profile.updateAttributes(protectedAttributes);
        Profile savedProfile = profileRepository.save(profile);

        eventPublisher.publish(ProfileUpdatedEvent.of(
                savedProfile.getId().toString(),
                savedProfile.getTenantId(),
                protectedAttributes));

        return mapToResponse(savedProfile);
    }

    private ProfileResponse mapToResponse(Profile profile) {
        // Unprotect PII fields for the API response
        var unprotectedAttributes = piiProtectionService.unprotect(profile.getAttributes());

        return ProfileResponse.builder()
                .id(profile.getId())
                .tenantId(profile.getTenantId())
                .handle(profile.getHandle().getValue())
                .attributes(unprotectedAttributes)
                .createdAt(profile.getCreatedAt())
                .updatedAt(profile.getUpdatedAt())
                .deletedAt(profile.getDeletedAt())
                .build();
    }

    public record Input(String tenantId, UUID id, UpdateProfileCommand command) {
    }
}
