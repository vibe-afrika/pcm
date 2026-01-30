package dev.vibeafrika.pcm.consent.api.rest;

import dev.vibeafrika.pcm.consent.application.dto.ConsentResponse;
import dev.vibeafrika.pcm.consent.application.dto.GrantConsentCommand;
import dev.vibeafrika.pcm.consent.application.dto.RevokeConsentCommand;
import dev.vibeafrika.pcm.consent.application.usecase.GetConsentHistoryUseCase;
import dev.vibeafrika.pcm.consent.application.usecase.GrantConsentUseCase;
import dev.vibeafrika.pcm.consent.application.usecase.RevokeConsentUseCase;
import dev.vibeafrika.pcm.consent.application.usecase.VerifyConsentUseCase;
import dev.vibeafrika.pcm.consent.domain.model.ConsentPurpose;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST Controller for Consent management.
 */
@RestController
@RequestMapping("/api/v1/consents")
@RequiredArgsConstructor
@Tag(name = "Consents", description = "User consent management and immutable ledger endpoints")
public class ConsentController {

    private final GrantConsentUseCase grantConsentUseCase;
    private final RevokeConsentUseCase revokeConsentUseCase;
    private final GetConsentHistoryUseCase getConsentHistoryUseCase;
    private final VerifyConsentUseCase verifyConsentUseCase;

    @GetMapping("/{profileId}/history")
    @Operation(summary = "Get full consent history for a profile")
    public java.util.List<ConsentResponse> getHistory(
            @PathVariable UUID profileId,
            @RequestParam(required = false) ConsentPurpose purpose) {
        return getConsentHistoryUseCase.execute(new GetConsentHistoryUseCase.Input(profileId, purpose));
    }

    @GetMapping("/{profileId}/verify")
    @Operation(summary = "Verify current consent status for a purpose")
    public ConsentResponse verifyConsent(
            @PathVariable UUID profileId,
            @RequestParam ConsentPurpose purpose) {
        return verifyConsentUseCase.execute(new VerifyConsentUseCase.Input(profileId, purpose));
    }

    @PostMapping("/{profileId}/grant")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Grant consent for a specific purpose")
    public ConsentResponse grantConsent(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @PathVariable UUID profileId,
            @Valid @RequestBody GrantConsentCommand command,
            HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");
        return grantConsentUseCase.execute(new GrantConsentUseCase.Input(tenantId, profileId, command, ipAddress, userAgent));
    }

    @PostMapping("/{profileId}/revoke")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Revoke consent for a specific purpose")
    public ConsentResponse revokeConsent(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @PathVariable UUID profileId,
            @Valid @RequestBody RevokeConsentCommand command,
            HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");
        return revokeConsentUseCase.execute(new RevokeConsentUseCase.Input(tenantId, profileId, command, ipAddress, userAgent));
    }
}
