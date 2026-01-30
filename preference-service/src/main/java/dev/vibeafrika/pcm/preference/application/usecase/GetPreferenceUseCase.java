package dev.vibeafrika.pcm.preference.application.usecase;

import dev.vibeafrika.pcm.common.usecase.UseCase;
import dev.vibeafrika.pcm.preference.application.dto.PreferenceResponse;
import dev.vibeafrika.pcm.preference.domain.repository.PreferenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetPreferenceUseCase implements UseCase<GetPreferenceUseCase.Input, PreferenceResponse> {

    private final PreferenceRepository preferenceRepository;

    @Override
    public PreferenceResponse execute(Input input) {
        return preferenceRepository.findByProfileId(input.profileId())
            .map(p -> PreferenceResponse.builder()
                .profileId(p.getProfileId())
                .tenantId(p.getTenantId())
                .settings(p.getSettings())
                .lastUpdated(p.getLastUpdated())
                .build())
            .orElseThrow(() -> new IllegalArgumentException("Preferences not found for profile " + input.profileId()));
    }

    public record Input(UUID profileId) {}
}
