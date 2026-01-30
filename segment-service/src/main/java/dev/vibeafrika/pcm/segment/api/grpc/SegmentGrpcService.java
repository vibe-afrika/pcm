package dev.vibeafrika.pcm.segment.api.grpc;

import dev.vibeafrika.pcm.segment.application.dto.SegmentResponse;
import dev.vibeafrika.pcm.segment.application.usecase.GetUserSegmentsUseCase;
import dev.vibeafrika.pcm.segment.domain.model.Segment;
import dev.vibeafrika.pcm.segment.domain.repository.SegmentRepository;
import dev.vibeafrika.pcm.grpc.segment.*;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * gRPC Service implementation for Segment management.
 */
@GrpcService
@RequiredArgsConstructor
public class SegmentGrpcService extends SegmentServiceGrpc.SegmentServiceImplBase {

    private final GetUserSegmentsUseCase getUserSegmentsUseCase;
    private final SegmentRepository segmentRepository;

    @Override
    public void getSegments(GetSegmentsRequest request, StreamObserver<GetSegmentsResponse> responseObserver) {
        try {
            SegmentResponse response = getUserSegmentsUseCase.execute(
                new GetUserSegmentsUseCase.Input(UUID.fromString(request.getProfileId()))
            );

            GetSegmentsResponse grpcResponse = GetSegmentsResponse.newBuilder()
                .addAllTags(response.getTags())
                .putAllScores(response.getScores())
                .setUpdatedAt(response.getLastUpdated().toEpochMilli())
                .build();

            responseObserver.onNext(grpcResponse);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(io.grpc.Status.INTERNAL
                .withDescription("Error retrieving segments: " + e.getMessage())
                .asRuntimeException());
        }
    }

    @Override
    public void belongsToSegment(BelongsToSegmentRequest request, StreamObserver<BelongsToSegmentResponse> responseObserver) {
        try {
            Segment segment = segmentRepository.findByProfileIdAndTenantId(
                UUID.fromString(request.getProfileId()), 
                request.getTenantId()
            ).orElseThrow(() -> new IllegalArgumentException("Segment record not found"));

            boolean belongs = segment.getTags().contains(request.getSegmentTag());
            double score = segment.getScores().getOrDefault(request.getSegmentTag(), belongs ? 1.0 : 0.0);

            BelongsToSegmentResponse grpcResponse = BelongsToSegmentResponse.newBuilder()
                .setBelongs(belongs)
                .setScore(score)
                .build();

            responseObserver.onNext(grpcResponse);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onNext(BelongsToSegmentResponse.newBuilder().setBelongs(false).build());
            responseObserver.onCompleted();
        }
    }

    @Override
    public void getProfilesInSegment(GetProfilesInSegmentRequest request, StreamObserver<GetProfilesInSegmentResponse> responseObserver) {
        try {
            List<Segment> segments = segmentRepository.findByTagAndTenantId(
                request.getSegmentTag(),
                request.getTenantId(),
                request.getPage(),
                request.getPageSize()
            );

            GetProfilesInSegmentResponse grpcResponse = GetProfilesInSegmentResponse.newBuilder()
                .addAllProfileIds(segments.stream().map(s -> s.getProfileId().toString()).collect(Collectors.toList()))
                .setPage(request.getPage())
                .setPageSize(request.getPageSize())
                .build();

            responseObserver.onNext(grpcResponse);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(io.grpc.Status.INTERNAL
                .withDescription("Error retrieving profiles in segment: " + e.getMessage())
                .asRuntimeException());
        }
    }
}
