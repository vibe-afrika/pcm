package dev.vibeafrika.pcm.preference.api.rest;

import dev.vibeafrika.pcm.preference.application.dto.PreferenceResponse;
import dev.vibeafrika.pcm.preference.application.usecase.GetPreferenceUseCase;
import dev.vibeafrika.pcm.preference.application.usecase.UpdatePreferenceUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/preferences")
@Tag(name = "Preferences", description = "User UX settings and preferences")
@RequiredArgsConstructor
public class PreferenceController {

    private final UpdatePreferenceUseCase updatePreferenceUseCase;
    private final GetPreferenceUseCase getPreferenceUseCase;

    @GetMapping("/{profileId}")
    @Operation(summary = "Get preferences for a profile")
    public PreferenceResponse getPreferences(@PathVariable UUID profileId) {
        return getPreferenceUseCase.execute(new GetPreferenceUseCase.Input(profileId));
    }

    @PatchMapping("/{profileId}")
    @Operation(summary = "Update preferences for a profile")
    public void updatePreferences(
            @PathVariable UUID profileId,
            @RequestHeader("X-Tenant-Id") String tenantId,
            @RequestBody Map<String, String> settings) {
        updatePreferenceUseCase.execute(new UpdatePreferenceUseCase.Input(tenantId, profileId, settings));
    }
}
