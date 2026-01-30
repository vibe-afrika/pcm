package dev.vibeafrika.pcm.segment.application.usecase;

import dev.vibeafrika.pcm.common.usecase.UseCase;
import dev.vibeafrika.pcm.segment.domain.event.SegmentEventPublisher;
import dev.vibeafrika.pcm.segment.domain.event.UserSegmentedEvent;
import dev.vibeafrika.pcm.segment.domain.model.Segment;
import dev.vibeafrika.pcm.segment.domain.repository.SegmentRepository;
import dev.vibeafrika.pcm.segment.domain.service.ClassificationRule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Use case to classify a user based on profile attributes.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ClassifyUserUseCase implements UseCase<ClassifyUserUseCase.Input, Void> {

    private final SegmentRepository segmentRepository;
    private final List<ClassificationRule> rules;
    private final SegmentEventPublisher eventPublisher;

    @Override
    public Void execute(Input input) {
        log.info("Classifying user {} for tenant {}", input.profileId, input.tenantId);

        // 1. Load or create segment record
        Segment segment = segmentRepository.findByProfileIdAndTenantId(input.profileId(), input.tenantId())
            .orElseGet(() -> Segment.create(input.tenantId(), input.profileId()));

        // 2. Evaluate all rules
        rules.forEach(rule -> {
            log.debug("Evaluating rule: {}", rule.getName());
            rule.evaluate(input.attributes(), segment);
        });

        // 3. Save classification
        Segment savedSegment = segmentRepository.save(segment);

        // 4. Publish event
        eventPublisher.publish(UserSegmentedEvent.of(
            savedSegment.getTenantId(),
            savedSegment.getId().toString(),
            savedSegment.getProfileId().toString(),
            savedSegment.getTags(),
            savedSegment.getScores()
        ));

        return null;
    }

    public record Input(String tenantId, UUID profileId, Map<String, Object> attributes) {}
}
