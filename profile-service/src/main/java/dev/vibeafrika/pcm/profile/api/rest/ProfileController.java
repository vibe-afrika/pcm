package dev.vibeafrika.pcm.profile.api.rest;

import dev.vibeafrika.pcm.common.domain.TenantId;
import dev.vibeafrika.pcm.profile.application.dto.CreateProfileCommand;
import dev.vibeafrika.pcm.profile.application.dto.ProfileResponse;
import dev.vibeafrika.pcm.profile.application.dto.UpdateProfileCommand;
import dev.vibeafrika.pcm.profile.application.usecase.CreateProfileUseCase;
import dev.vibeafrika.pcm.profile.application.usecase.EraseProfileUseCase;
import dev.vibeafrika.pcm.profile.application.usecase.GetProfileUseCase;
import dev.vibeafrika.pcm.profile.application.usecase.UpdateProfileUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST Controller for Profile management.
 */
@RestController
@RequestMapping("/api/v1/profiles")
@RequiredArgsConstructor
@Tag(name = "Profiles", description = "User profile management endpoints")
public class ProfileController {

    private final CreateProfileUseCase createProfileUseCase;
    private final GetProfileUseCase getProfileUseCase;
    private final UpdateProfileUseCase updateProfileUseCase;
    private final EraseProfileUseCase eraseProfileUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new profile")
    public ProfileResponse createProfile(
            @RequestHeader("X-Tenant-Id") String tenantId,
            @RequestBody CreateProfileCommand command) {
        return createProfileUseCase.execute(new CreateProfileUseCase.Input(TenantId.of(tenantId), command));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get profile by ID")
    public ProfileResponse getProfile(
            @RequestHeader("X-Tenant-Id") String tenantId,
            @PathVariable("id") String id) {
        return getProfileUseCase.execute(new GetProfileUseCase.Input(tenantId, UUID.fromString(id)));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update profile attributes")
    public ProfileResponse updateProfile(
            @RequestHeader("X-Tenant-Id") String tenantId,
            @PathVariable("id") String id,
            @RequestBody UpdateProfileCommand command) {
        return updateProfileUseCase.execute(new UpdateProfileUseCase.Input(tenantId, UUID.fromString(id), command));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Erase profile (GDPR right to be forgotten)")
    public void eraseProfile(
            @RequestHeader("X-Tenant-Id") String tenantId,
            @PathVariable("id") String id) {
        eraseProfileUseCase.execute(new EraseProfileUseCase.Input(tenantId, UUID.fromString(id)));
    }
}
