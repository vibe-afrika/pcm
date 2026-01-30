package dev.vibeafrika.pcm.consent.api.grpc;

import dev.vibeafrika.pcm.consent.application.dto.ConsentResponse;
import dev.vibeafrika.pcm.consent.application.usecase.GetConsentHistoryUseCase;
import dev.vibeafrika.pcm.consent.application.usecase.VerifyConsentUseCase;

import dev.vibeafrika.pcm.grpc.consent.*;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * gRPC Service implementation for Consent management.
 * Provides internal service-to-service communication.
 */
@GrpcService
@RequiredArgsConstructor
public class ConsentGrpcService extends ConsentServiceGrpc.ConsentServiceImplBase {

    private final GetConsentHistoryUseCase getConsentHistoryUseCase;
    private final VerifyConsentUseCase verifyConsentUseCase;

    @Override
    public void getConsentStatus(GetConsentStatusRequest request, StreamObserver<ConsentStatusResponse> responseObserver) {
        try {
            ConsentResponse response = verifyConsentUseCase.execute(
                new VerifyConsentUseCase.Input(
                    UUID.fromString(request.getProfileId()),
                    mapToDomainPurpose(request.getPurpose())
                )
            );

            ConsentStatusResponse grpcResponse = ConsentStatusResponse.newBuilder()
                .setGranted(response.isGranted())
                .setVersion(response.getVersion())
                .setTimestamp(response.getTimestamp().toEpochMilli())
                .setProofHash(response.getProofHash())
                .build();

            responseObserver.onNext(grpcResponse);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(io.grpc.Status.INTERNAL
                .withDescription("Error retrieving consent status: " + e.getMessage())
                .asRuntimeException());
        }
    }

    @Override
    public void getAllConsents(GetAllConsentsRequest request, StreamObserver<GetAllConsentsResponse> responseObserver) {
        try {
            List<ConsentResponse> history = getConsentHistoryUseCase.execute(
                new GetConsentHistoryUseCase.Input(UUID.fromString(request.getProfileId()), null)
            );

            GetAllConsentsResponse grpcResponse = GetAllConsentsResponse.newBuilder()
                .addAllConsents(history.stream().map(this::mapToGrpcRecord).collect(Collectors.toList()))
                .build();

            responseObserver.onNext(grpcResponse);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(io.grpc.Status.INTERNAL
                .withDescription("Error retrieving all consents: " + e.getMessage())
                .asRuntimeException());
        }
    }

    @Override
    public void getConsentHistory(GetConsentHistoryRequest request, StreamObserver<ConsentHistoryResponse> responseObserver) {
        try {
            List<ConsentResponse> history = getConsentHistoryUseCase.execute(
                new GetConsentHistoryUseCase.Input(
                    UUID.fromString(request.getProfileId()),
                    mapToDomainPurpose(request.getPurpose())
                )
            );

            ConsentHistoryResponse grpcResponse = ConsentHistoryResponse.newBuilder()
                .addAllHistory(history.stream().map(this::mapToGrpcRecord).collect(Collectors.toList()))
                .build();

            responseObserver.onNext(grpcResponse);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(io.grpc.Status.INTERNAL
                .withDescription("Error retrieving consent history: " + e.getMessage())
                .asRuntimeException());
        }
    }

    @Override
    public void verifyConsent(VerifyConsentRequest request, StreamObserver<VerifyConsentResponse> responseObserver) {
        try {
            ConsentResponse response = verifyConsentUseCase.execute(
                new VerifyConsentUseCase.Input(
                    UUID.fromString(request.getProfileId()),
                    mapToDomainPurpose(request.getPurpose())
                )
            );

            VerifyConsentResponse grpcResponse = VerifyConsentResponse.newBuilder()
                .setHasConsent(response.isGranted())
                .setGrantedAt(response.getTimestamp().toEpochMilli())
                .build();

            responseObserver.onNext(grpcResponse);
            responseObserver.onCompleted();
        } catch (Exception e) {
            // If not found, return false instead of error (optional design choice, usually verify returns bool)
            VerifyConsentResponse grpcResponse = VerifyConsentResponse.newBuilder()
                .setHasConsent(false)
                .build();
            responseObserver.onNext(grpcResponse);
            responseObserver.onCompleted();
        }
    }

    private ConsentRecord mapToGrpcRecord(ConsentResponse response) {
        return ConsentRecord.newBuilder()
            .setId(response.getId().toString())
            .setProfileId(response.getProfileId().toString())
            .setPurpose(mapToGrpcPurpose(response.getPurpose()))
            .setGranted(response.isGranted())
            .setVersion(response.getVersion())
            .setTimestamp(response.getTimestamp().toEpochMilli())
            .setProofHash(response.getProofHash())
            .build();
    }

    private ConsentPurpose mapToGrpcPurpose(dev.vibeafrika.pcm.consent.domain.model.ConsentPurpose purpose) {
        return ConsentPurpose.valueOf(purpose.name());
    }

    private dev.vibeafrika.pcm.consent.domain.model.ConsentPurpose mapToDomainPurpose(dev.vibeafrika.pcm.grpc.consent.ConsentPurpose purpose) {
        return dev.vibeafrika.pcm.consent.domain.model.ConsentPurpose.valueOf(purpose.name());
    }
}
