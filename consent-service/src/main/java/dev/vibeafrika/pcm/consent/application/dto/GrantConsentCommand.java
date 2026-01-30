package dev.vibeafrika.pcm.consent.application.dto;

import dev.vibeafrika.pcm.consent.domain.model.ConsentPurpose;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

/**
 * Command to grant consent.
 */
@Getter
@Builder
public class GrantConsentCommand {
    
    @NotNull
    private final ConsentPurpose purpose;
    
    @NotBlank
    private final String version;
    
    @NotBlank
    private final String consentText;
    
    private final Map<String, Object> metadata;
}
