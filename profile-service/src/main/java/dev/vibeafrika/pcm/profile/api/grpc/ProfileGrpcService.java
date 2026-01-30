package dev.vibeafrika.pcm.profile.api.grpc;

import dev.vibeafrika.pcm.grpc.profile.*;
import dev.vibeafrika.pcm.profile.application.dto.ProfileResponse;
import dev.vibeafrika.pcm.profile.application.usecase.GetProfileUseCase;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * gRPC Service implementation for Profile management.
 * Provides internal service-to-service communication.
 */
@GrpcService
@RequiredArgsConstructor
public class ProfileGrpcService extends ProfileServiceGrpc.ProfileServiceImplBase {

    private final GetProfileUseCase getProfileUseCase;

    @Override
    public void getProfile(GetProfileRequest request, StreamObserver<GetProfileResponse> responseObserver) {
        try {
            ProfileResponse response = getProfileUseCase.execute(
                    new GetProfileUseCase.Input(request.getTenantId(), UUID.fromString(request.getProfileId())));

            GetProfileResponse grpcResponse = GetProfileResponse.newBuilder()
                    .setProfile(mapToGrpcProfile(response))
                    .build();

            responseObserver.onNext(grpcResponse);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(io.grpc.Status.INTERNAL
                    .withDescription("Error retrieving profile: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    private Profile mapToGrpcProfile(ProfileResponse profile) {
        Profile.Builder builder = Profile.newBuilder()
                .setId(profile.getId().toString())
                .setTenantId(profile.getTenantId())
                .setHandle(profile.getHandle())
                .setCreatedAt(profile.getCreatedAt().toEpochMilli())
                .setUpdatedAt(profile.getUpdatedAt().toEpochMilli())
                .setDeletedAt(profile.getDeletedAt() != null ? profile.getDeletedAt().toEpochMilli() : 0L);

        if (profile.getAttributes() != null) {
            builder.putAllAttributes(profile.getAttributes().entrySet().stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            e -> String.valueOf(e.getValue()))));
        }

        return builder.build();
    }
}
