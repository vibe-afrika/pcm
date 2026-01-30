package dev.vibeafrika.pcm.profile.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * Data Transfer Object for profile response.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileResponse {
    private UUID id;
    private String tenantId;
    private String handle;
    private Map<String, Object> attributes;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;
}
