package dev.vibeafrika.pcm.gateway.application.service;

import com.google.common.util.concurrent.ListenableFuture;
import dev.vibeafrika.pcm.gateway.application.dto.UserContextResponse;
import dev.vibeafrika.pcm.grpc.consent.ConsentRecord;
import dev.vibeafrika.pcm.grpc.consent.ConsentServiceGrpc;
import dev.vibeafrika.pcm.grpc.consent.GetAllConsentsRequest;
import dev.vibeafrika.pcm.grpc.consent.GetAllConsentsResponse;
import dev.vibeafrika.pcm.grpc.preference.GetPreferencesRequest;
import dev.vibeafrika.pcm.grpc.preference.GetPreferencesResponse;
import dev.vibeafrika.pcm.grpc.preference.NotificationPreferences;
import dev.vibeafrika.pcm.grpc.preference.PreferenceServiceGrpc;
import dev.vibeafrika.pcm.grpc.preference.Preferences;
import dev.vibeafrika.pcm.grpc.profile.GetProfileRequest;
import dev.vibeafrika.pcm.grpc.profile.GetProfileResponse;
import dev.vibeafrika.pcm.grpc.profile.Profile;
import dev.vibeafrika.pcm.grpc.profile.ProfileServiceGrpc;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserContextService {

        @net.devh.boot.grpc.client.inject.GrpcClient("profile-service")
        private ProfileServiceGrpc.ProfileServiceFutureStub profileServiceStub;

        @net.devh.boot.grpc.client.inject.GrpcClient("preference-service")
        private PreferenceServiceGrpc.PreferenceServiceFutureStub preferenceServiceStub;

        @net.devh.boot.grpc.client.inject.GrpcClient("consent-service")
        private ConsentServiceGrpc.ConsentServiceFutureStub consentServiceStub;

        // Dedicated scheduler for gRPC callbacks to avoid blocking Netty event loop
        private final Scheduler grpcScheduler = Schedulers.boundedElastic();

        public Mono<UserContextResponse> getUserContext(String profileId, String tenantId) {
                log.info("Aggregating context for user: {} (tenant: {})", profileId, tenantId);

                // 1. Profile Service Call
                Mono<GetProfileResponse> profileMono = toMono(
                                profileServiceStub.getProfile(GetProfileRequest.newBuilder()
                                                .setProfileId(profileId)
                                                .setTenantId(tenantId)
                                                .build()))
                                .onErrorResume(e -> {
                                        log.warn("Failed to fetch profile for user {}: {}", profileId, e.getMessage());
                                        return Mono.just(GetProfileResponse.newBuilder().build());
                                });

                // 2. Preference Service Call
                Mono<GetPreferencesResponse> preferenceMono = toMono(
                                preferenceServiceStub.getPreferences(GetPreferencesRequest.newBuilder()
                                                .setProfileId(profileId)
                                                .setTenantId(tenantId)
                                                .build()))
                                .onErrorReturn(GetPreferencesResponse.getDefaultInstance());

                // 3. Consent Service Call
                Mono<GetAllConsentsResponse> consentMono = toMono(
                                consentServiceStub.getAllConsents(GetAllConsentsRequest.newBuilder()
                                                .setProfileId(profileId)
                                                .setTenantId(tenantId)
                                                .build()))
                                .onErrorReturn(GetAllConsentsResponse.getDefaultInstance());

                // 4. Reactive Aggregation (Zip)
                return Mono.zip(profileMono, preferenceMono, consentMono)
                                .map(tuple -> {
                                        GetProfileResponse profileRes = tuple.getT1();
                                        GetPreferencesResponse prefRes = tuple.getT2();
                                        GetAllConsentsResponse consentRes = tuple.getT3();

                                        UserContextResponse.UserContextResponseBuilder builder = UserContextResponse
                                                        .builder();

                                        if (profileRes.hasProfile()) {
                                                builder.profile(mapProfile(profileRes.getProfile()));
                                        }

                                        if (prefRes.hasPreferences()) {
                                                builder.preferences(mapPreferences(prefRes.getPreferences()));
                                        }

                                        return builder
                                                        .consents(consentRes.getConsentsList().stream()
                                                                        .map(this::mapConsent)
                                                                        .collect(Collectors.toList()))
                                                        .build();
                                });
        }

        // Production-ready helper: uses bounded elastic scheduler for callbacks
        private <T> Mono<T> toMono(ListenableFuture<T> listenableFuture) {
                return Mono.create(sink -> {
                        listenableFuture.addListener(() -> {
                                try {
                                        sink.success(listenableFuture.get());
                                } catch (InterruptedException | ExecutionException e) {
                                        sink.error(e);
                                }
                        }, runnable -> grpcScheduler.schedule(runnable));
                });
        }

        private UserContextResponse.ProfileView mapProfile(Profile profile) {
                return UserContextResponse.ProfileView.builder()
                                .id(profile.getId())
                                .handle(profile.getHandle())
                                .attributes(profile.getAttributesMap())
                                .build();
        }

        private UserContextResponse.PreferenceView mapPreferences(Preferences preferences) {
                // Complete mapping for production
                return UserContextResponse.PreferenceView.builder()
                                .language(preferences.getLanguage())
                                .theme(preferences.getTheme())
                                .notifications(convertNotificationPreferences(preferences.getNotifications()))
                                .build();
        }

        private Map<String, Boolean> convertNotificationPreferences(
                        NotificationPreferences np) {
                if (np == null)
                        return Collections.emptyMap();

                Map<String, Boolean> map = new HashMap<>();
                map.put("email", np.getEmailEnabled());
                map.put("sms", np.getSmsEnabled());
                map.put("push", np.getPushEnabled());
                map.put("marketing", np.getMarketingEnabled());
                map.put("updates", np.getProductUpdatesEnabled());
                return map;
        }

        private UserContextResponse.ConsentView mapConsent(ConsentRecord consentRecord) {
                return UserContextResponse.ConsentView.builder()
                                .purpose(consentRecord.getPurpose().name())
                                .granted(consentRecord.getGranted())
                                .version(consentRecord.getVersion())
                                .timestamp(consentRecord.getTimestamp())
                                .build();
        }
}
