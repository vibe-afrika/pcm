package dev.vibeafrika.pcm.consent.application.usecase;

import dev.vibeafrika.pcm.common.usecase.UseCase;
import dev.vibeafrika.pcm.consent.application.dto.ConsentResponse;
import dev.vibeafrika.pcm.consent.application.dto.GrantConsentCommand;
import dev.vibeafrika.pcm.consent.domain.event.ConsentEventPublisher;
import dev.vibeafrika.pcm.consent.domain.event.ConsentGrantedEvent;
import dev.vibeafrika.pcm.consent.domain.model.Consent;
import dev.vibeafrika.pcm.consent.domain.repository.ConsentRepository;
import dev.vibeafrika.pcm.consent.domain.service.ProofHashGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

/**
 * Use case to grant consent.
 */
@Service
@RequiredArgsConstructor
public class GrantConsentUseCase implements UseCase<GrantConsentUseCase.Input, ConsentResponse> {

    private final ConsentRepository consentRepository;
    private final ProofHashGenerator proofHashGenerator;
    private final ConsentEventPublisher eventPublisher;

    @Override
    @Transactional
    public ConsentResponse execute(Input input) {
        Instant now = Instant.now();

        // 1. Generate proof hash
        String proofHash = proofHashGenerator.generate(
            input.profileId,
            input.command.getPurpose(),
            true,
            input.command.getVersion(),
            input.command.getConsentText(),
            now,
            input.ipAddress,
            input.userAgent
        );

        // 2. Create and save consent entry
        Consent consent = Consent.grant(
            input.tenantId,
            input.profileId,
            input.command.getPurpose(),
            input.command.getVersion(),
            input.command.getConsentText(),
            input.ipAddress,
            input.userAgent,
            proofHash
        );

        Consent savedConsent = consentRepository.save(consent);

        // 3. Publish event
        eventPublisher.publish(ConsentGrantedEvent.of(
            savedConsent.getTenantId(),
            savedConsent.getId().toString(),
            savedConsent.getProfileId().toString(),
            savedConsent.getPurpose().name(),
            savedConsent.getVersion(),
            savedConsent.getProofHash()
        ));

        return mapToResponse(savedConsent);
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

    public record Input(String tenantId, UUID profileId, GrantConsentCommand command, String ipAddress, String userAgent) {}
}
