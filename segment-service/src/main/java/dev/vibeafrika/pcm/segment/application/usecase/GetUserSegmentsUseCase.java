package dev.vibeafrika.pcm.segment.application.usecase;

import dev.vibeafrika.pcm.common.usecase.UseCase;
import dev.vibeafrika.pcm.segment.application.dto.SegmentResponse;
import dev.vibeafrika.pcm.segment.domain.repository.SegmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetUserSegmentsUseCase implements UseCase<GetUserSegmentsUseCase.Input, SegmentResponse> {

    private final SegmentRepository segmentRepository;

    @Override
    public SegmentResponse execute(Input input) {
        return segmentRepository.findByProfileId(input.profileId())
            .map(segment -> SegmentResponse.builder()
                .profileId(segment.getProfileId())
                .tenantId(segment.getTenantId())
                .tags(segment.getTags())
                .scores(segment.getScores())
                .lastUpdated(segment.getLastUpdated())
                .build())
            .orElseThrow(() -> new IllegalArgumentException("No segments found for profile " + input.profileId()));
    }

    public record Input(UUID profileId) {}
}
