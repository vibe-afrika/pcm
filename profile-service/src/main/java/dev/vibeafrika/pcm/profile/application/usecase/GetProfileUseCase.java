package dev.vibeafrika.pcm.profile.application.usecase;

import dev.vibeafrika.pcm.common.usecase.UseCase;
import dev.vibeafrika.pcm.profile.application.dto.ProfileResponse;
import dev.vibeafrika.pcm.profile.application.service.PIIProtectionService;
import dev.vibeafrika.pcm.profile.domain.model.Profile;
import dev.vibeafrika.pcm.profile.domain.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Use case to retrieve a profile by ID.
 */
@Service
@RequiredArgsConstructor
public class GetProfileUseCase implements UseCase<GetProfileUseCase.Input, ProfileResponse> {

    private final ProfileRepository profileRepository;
    private final PIIProtectionService piiProtectionService;

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "profiles", key = "#input.tenantId + ':' + #input.id")
    public ProfileResponse execute(Input input) {
        Profile profile = profileRepository.findByIdAndTenantId(input.id, input.tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Profile not found: " + input.id));

        return mapToResponse(profile);
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

    public record Input(String tenantId, UUID id) {
    }
}
