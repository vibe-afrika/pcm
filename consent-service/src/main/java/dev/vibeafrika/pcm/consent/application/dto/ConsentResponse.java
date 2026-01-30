package dev.vibeafrika.pcm.consent.application.dto;

import dev.vibeafrika.pcm.consent.domain.model.ConsentPurpose;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

/**
 * Response representing a consent record.
 */
@Getter
@Builder
public class ConsentResponse {
    private final UUID id;
    private final String tenantId;
    private final UUID profileId;
    private final ConsentPurpose purpose;
    private final boolean granted;
    private final String version;
    private final Instant timestamp;
    private final String proofHash;
}
