package dev.vibeafrika.pcm.gateway.application.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
@Builder
public class UnifiedProfileResponse {
    private final String id;
    private final String handle;
    private final Map<String, Object> attributes;
    private final List<String> segmentTags;
    private final Map<String, String> userPreferences;
}
