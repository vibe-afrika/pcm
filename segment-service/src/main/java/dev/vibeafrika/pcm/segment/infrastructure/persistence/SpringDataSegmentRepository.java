package dev.vibeafrika.pcm.segment.infrastructure.persistence;

import dev.vibeafrika.pcm.segment.domain.model.Segment;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data Elasticsearch repository for Segment documents.
 */
public interface SpringDataSegmentRepository extends ElasticsearchRepository<Segment, UUID> {
    Optional<Segment> findByProfileId(UUID profileId);
    Optional<Segment> findByProfileIdAndTenantId(UUID profileId, String tenantId);
    Page<Segment> findByTagsContainingAndTenantId(String tag, String tenantId, Pageable pageable);
}
