package dev.vibeafrika.pcm.preference.application.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Getter
@Builder
public class PreferenceResponse {
    private final UUID profileId;
    private final String tenantId;
    private final Map<String, String> settings;
    private final Instant lastUpdated;
}
