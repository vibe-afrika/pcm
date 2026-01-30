package dev.vibeafrika.pcm.consent.application.usecase;

import dev.vibeafrika.pcm.common.usecase.UseCase;
import dev.vibeafrika.pcm.consent.application.dto.ConsentResponse;
import dev.vibeafrika.pcm.consent.domain.model.ConsentPurpose;
import dev.vibeafrika.pcm.consent.domain.repository.ConsentRepository;
import dev.vibeafrika.pcm.consent.domain.model.Consent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Use case to verify current consent status.
 */
@Service
@RequiredArgsConstructor
public class VerifyConsentUseCase implements UseCase<VerifyConsentUseCase.Input, ConsentResponse> {

    private final ConsentRepository consentRepository;

    @Override
    @Transactional(readOnly = true)
    public ConsentResponse execute(Input input) {
        return consentRepository.findLatestByProfileIdAndPurpose(input.profileId(), input.purpose())
            .map(this::mapToResponse)
            .orElseThrow(() -> new IllegalArgumentException("No consent found for profile " + input.profileId() + " and purpose " + input.purpose()));
    }

    private ConsentResponse mapToResponse(Consent consent) {
        return ConsentResponse.builder()
            .id(consent.getId())
            .tenantId(consent.getTenantId())
            .profileId(consent.getProfileId())
            .purpose(consent.getPurpose())
            .granted(consent.isGranted())
            .version(consent.getVersion())
            .timestamp(consent.getTimestamp())
            .proofHash(consent.getProofHash())
            .build();
    }

    public record Input(UUID profileId, ConsentPurpose purpose) {}
}
