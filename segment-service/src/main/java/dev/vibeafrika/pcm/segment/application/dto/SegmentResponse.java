package dev.vibeafrika.pcm.segment.application.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Getter
@Builder
public class SegmentResponse {
    private final UUID profileId;
    private final String tenantId;
    private final Set<String> tags;
    private final Map<String, Double> scores;
    private final Instant lastUpdated;
}
