package dev.vibeafrika.pcm.preference.api.grpc;

import dev.vibeafrika.pcm.preference.application.dto.PreferenceResponse;
import dev.vibeafrika.pcm.preference.application.usecase.GetPreferenceUseCase;
import dev.vibeafrika.pcm.preference.application.usecase.UpdatePreferenceUseCase;
import dev.vibeafrika.pcm.grpc.preference.*;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.UUID;

/**
 * gRPC Service implementation for Preference management.
 */
@GrpcService
@RequiredArgsConstructor
public class PreferenceGrpcService extends PreferenceServiceGrpc.PreferenceServiceImplBase {

    private final GetPreferenceUseCase getPreferenceUseCase;
    private final UpdatePreferenceUseCase updatePreferenceUseCase;

    @Override
    public void getPreferences(GetPreferencesRequest request, StreamObserver<GetPreferencesResponse> responseObserver) {
        try {
            PreferenceResponse response = getPreferenceUseCase.execute(
                new GetPreferenceUseCase.Input(UUID.fromString(request.getProfileId()))
            );

            Preferences grpcPrefs = Preferences.newBuilder()
                .setProfileId(response.getProfileId().toString())
                .setLanguage(response.getSettings().getOrDefault("language", "en"))
                .setTheme(response.getSettings().getOrDefault("theme", "auto"))
                .setNotifications(NotificationPreferences.newBuilder()
                    .setEmailEnabled(Boolean.parseBoolean(response.getSettings().getOrDefault("notifications.email", "true")))
                    .setPushEnabled(Boolean.parseBoolean(response.getSettings().getOrDefault("notifications.push", "true")))
                    .build())
                .putAllCustom(response.getSettings())
                .setUpdatedAt(response.getLastUpdated().toEpochMilli())
                .build();

            responseObserver.onNext(GetPreferencesResponse.newBuilder().setPreferences(grpcPrefs).build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(io.grpc.Status.NOT_FOUND
                .withDescription("Preferences not found: " + e.getMessage())
                .asRuntimeException());
        }
    }

    @Override
    public void getPreference(GetPreferenceRequest request, StreamObserver<GetPreferenceResponse> responseObserver) {
        try {
            PreferenceResponse response = getPreferenceUseCase.execute(
                new GetPreferenceUseCase.Input(UUID.fromString(request.getProfileId()))
            );

            String value = response.getSettings().getOrDefault(request.getKey(), "");

            responseObserver.onNext(GetPreferenceResponse.newBuilder()
                .setKey(request.getKey())
                .setValue(value)
                .build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onNext(GetPreferenceResponse.newBuilder().setKey(request.getKey()).setValue("").build());
            responseObserver.onCompleted();
        }
    }

    @Override
    public void updatePreferences(UpdatePreferencesRequest request, StreamObserver<UpdatePreferencesResponse> responseObserver) {
        try {
            updatePreferenceUseCase.execute(new UpdatePreferenceUseCase.Input(
                request.getTenantId(),
                UUID.fromString(request.getProfileId()),
                request.getPreferencesMap()
            ));

            responseObserver.onNext(UpdatePreferencesResponse.newBuilder()
                .setSuccess(true)
                .setUpdatedAt(System.currentTimeMillis())
                .build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(io.grpc.Status.INTERNAL
                .withDescription("Failed to update preferences: " + e.getMessage())
                .asRuntimeException());
        }
    }
}
