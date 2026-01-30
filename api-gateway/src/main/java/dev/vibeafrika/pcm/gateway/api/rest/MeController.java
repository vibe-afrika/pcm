package dev.vibeafrika.pcm.gateway.api.rest;

import dev.vibeafrika.pcm.gateway.application.dto.UnifiedProfileResponse;
import dev.vibeafrika.pcm.grpc.preference.PreferenceServiceGrpc;
import dev.vibeafrika.pcm.grpc.preference.GetPreferencesRequest;
import dev.vibeafrika.pcm.grpc.profile.ProfileServiceGrpc;
import dev.vibeafrika.pcm.grpc.profile.GetProfileRequest;
import dev.vibeafrika.pcm.grpc.segment.SegmentServiceGrpc;
import dev.vibeafrika.pcm.grpc.segment.GetSegmentsRequest;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.HashMap;
import java.util.Map;

/**
 * Aggregator controller to provide a unified view of the user.
 * Interacts with backend services via high-performance gRPC.
 */
@RestController
@RequestMapping("/api/v1/me")
@RequiredArgsConstructor
public class MeController {

    @GrpcClient("profile-service")
    private ProfileServiceGrpc.ProfileServiceBlockingStub profileStub;

    @GrpcClient("segment-service")
    private SegmentServiceGrpc.SegmentServiceBlockingStub segmentStub;

    @GrpcClient("preference-service")
    private PreferenceServiceGrpc.PreferenceServiceBlockingStub preferenceStub;

    @GetMapping
    public Mono<UnifiedProfileResponse> getMyProfile(
            @AuthenticationPrincipal Jwt jwt,
            @RequestHeader("X-Tenant-Id") String tenantId) {

        String profileId = jwt.getSubject();

        return Mono.fromCallable(() -> {
            // Call Profile Service
            var profileRes = profileStub.getProfile(GetProfileRequest.newBuilder()
                    .setProfileId(profileId)
                    .setTenantId(tenantId)
                    .build());

            // Call Segment Service
            var segmentRes = segmentStub.getSegments(GetSegmentsRequest.newBuilder()
                    .setProfileId(profileId)
                    .setTenantId(tenantId)
                    .build());

            // Call Preference Service
            var prefRes = preferenceStub.getPreferences(GetPreferencesRequest.newBuilder()
                    .setProfileId(profileId)
                    .setTenantId(tenantId)
                    .build());

            // Map Avro attributes back to Map
            Map<String, Object> attrs = new HashMap<>(profileRes.getProfile().getAttributesMap());

            return UnifiedProfileResponse.builder()
                    .id(profileRes.getProfile().getId())
                    .handle(profileRes.getProfile().getHandle())
                    .attributes(attrs)
                    .segmentTags(segmentRes.getTagsList())
                    .userPreferences(prefRes.getPreferences().getCustomMap())
                    .build();
        }).subscribeOn(Schedulers.boundedElastic());
    }
}
