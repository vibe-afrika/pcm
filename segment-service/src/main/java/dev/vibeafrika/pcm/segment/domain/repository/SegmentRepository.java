package dev.vibeafrika.pcm.segment.domain.repository;

import dev.vibeafrika.pcm.segment.domain.model.Segment;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository port for Segment persistence.
 */
public interface SegmentRepository {
    Segment save(Segment segment);
    Optional<Segment> findByProfileId(UUID profileId);
    Optional<Segment> findByProfileIdAndTenantId(UUID profileId, String tenantId);
    java.util.List<Segment> findByTagAndTenantId(String tag, String tenantId, int page, int size);
}
