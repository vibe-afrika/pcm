package dev.vibeafrika.pcm.consent.application.usecase;

import dev.vibeafrika.pcm.common.usecase.UseCase;
import dev.vibeafrika.pcm.consent.application.dto.ConsentResponse;
import dev.vibeafrika.pcm.consent.domain.model.Consent;
import dev.vibeafrika.pcm.consent.domain.model.ConsentPurpose;
import dev.vibeafrika.pcm.consent.domain.repository.ConsentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Use case to retrieve consent history.
 */
@Service
@RequiredArgsConstructor
public class GetConsentHistoryUseCase implements UseCase<GetConsentHistoryUseCase.Input, List<ConsentResponse>> {

    private final ConsentRepository consentRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ConsentResponse> execute(Input input) {
        List<Consent> history;
        if (input.purpose() != null) {
            history = consentRepository.findHistoryByProfileIdAndPurpose(input.profileId(), input.purpose());
        } else {
            history = consentRepository.findHistoryByProfileId(input.profileId());
        }

        return history.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
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
