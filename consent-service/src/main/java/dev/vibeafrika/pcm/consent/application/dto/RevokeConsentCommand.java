package dev.vibeafrika.pcm.consent.application.dto;

import dev.vibeafrika.pcm.consent.domain.model.ConsentPurpose;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

/**
 * Command to revoke consent.
 */
@Getter
@Builder
public class RevokeConsentCommand {
    
    @NotNull
    private final ConsentPurpose purpose;
    
    @NotBlank
    private final String version;
    
    @NotBlank
    private final String revocationReason;
}
