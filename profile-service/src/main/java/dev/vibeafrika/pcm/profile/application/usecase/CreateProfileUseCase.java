package dev.vibeafrika.pcm.profile.application.usecase;

import dev.vibeafrika.pcm.common.domain.TenantId;
import dev.vibeafrika.pcm.common.usecase.UseCase;
import dev.vibeafrika.pcm.profile.application.dto.CreateProfileCommand;
import dev.vibeafrika.pcm.profile.application.dto.ProfileResponse;
import dev.vibeafrika.pcm.profile.application.service.PIIProtectionService;
import dev.vibeafrika.pcm.profile.domain.event.ProfileCreatedEvent;
import dev.vibeafrika.pcm.profile.domain.event.ProfileEventPublisher;
import dev.vibeafrika.pcm.profile.domain.model.Handle;
import dev.vibeafrika.pcm.profile.domain.model.Profile;
import dev.vibeafrika.pcm.profile.domain.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use case to create a new profile.
 */
@Service
@RequiredArgsConstructor
public class CreateProfileUseCase implements UseCase<CreateProfileUseCase.Input, ProfileResponse> {

    private final ProfileRepository profileRepository;
    private final ProfileEventPublisher eventPublisher;
    private final PIIProtectionService piiProtectionService;

    @Override
    @Transactional
    public ProfileResponse execute(Input input) {
        Handle handle = Handle.of(input.command.getHandle());

        if (profileRepository.existsByHandle(handle)) {
            throw new IllegalArgumentException("Handle already exists: " + handle.getValue());
        }

        // Protect PII fields before entity creation
        var protectedAttributes = piiProtectionService.protect(input.command.getAttributes());

        Profile profile;
        if (input.command.getId() != null && !input.command.getId().isBlank()) {
            profile = Profile.createWithId(
                    java.util.UUID.fromString(input.command.getId()),
                    input.tenantId,
                    handle,
                    protectedAttributes);
        } else {
            profile = Profile.create(
                    input.tenantId,
                    handle,
                    protectedAttributes);
        }

        Profile savedProfile = profileRepository.save(profile);

        eventPublisher.publish(ProfileCreatedEvent.of(
                savedProfile.getId().toString(),
                savedProfile.getTenantId(),
                savedProfile.getHandle().getValue(),
                savedProfile.getAttributes()));

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

    public record Input(TenantId tenantId, CreateProfileCommand command) {
    }
}
