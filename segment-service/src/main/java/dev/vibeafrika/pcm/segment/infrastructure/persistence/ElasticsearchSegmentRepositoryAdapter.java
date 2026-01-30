package dev.vibeafrika.pcm.segment.infrastructure.persistence;

import dev.vibeafrika.pcm.segment.domain.model.Segment;
import dev.vibeafrika.pcm.segment.domain.repository.SegmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Elasticsearch implementation of SegmentRepository.
 */
@Repository
@RequiredArgsConstructor
public class ElasticsearchSegmentRepositoryAdapter implements SegmentRepository {

    private final SpringDataSegmentRepository repository;

    @Override
    public Segment save(Segment segment) {
        return repository.save(segment);
    }

    @Override
    public Optional<Segment> findByProfileId(UUID profileId) {
        return repository.findByProfileId(profileId);
    }

    @Override
    public Optional<Segment> findByProfileIdAndTenantId(UUID profileId, String tenantId) {
        return repository.findByProfileIdAndTenantId(profileId, tenantId);
    }

    @Override
    public List<Segment> findByTagAndTenantId(String tag, String tenantId, int page, int size) {
        return repository.findByTagsContainingAndTenantId(tag, tenantId, PageRequest.of(page, size))
            .getContent();
    }
}
