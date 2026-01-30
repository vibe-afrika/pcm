package dev.vibeafrika.pcm.gateway.api.rest;

import dev.vibeafrika.pcm.gateway.application.dto.UserContextResponse;
import dev.vibeafrika.pcm.gateway.application.service.UserContextService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserContextController {

    private final UserContextService userContextService;

    @GetMapping("/me")
    public Mono<UserContextResponse> getMyContext(
            @AuthenticationPrincipal Jwt jwt,
            @RequestHeader(value = "X-Tenant-Id", defaultValue = "default") String tenantId) {

        String profileId = jwt.getClaim("sub"); // In Keycloak, 'sub' is usually the user ID.
                                                // Adjust if you store PCM profile ID in a different claim.
        return userContextService.getUserContext(profileId, tenantId);
    }
}
