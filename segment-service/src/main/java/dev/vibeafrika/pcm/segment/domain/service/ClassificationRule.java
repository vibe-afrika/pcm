package dev.vibeafrika.pcm.segment.domain.service;

import dev.vibeafrika.pcm.segment.domain.model.Segment;
import java.util.Map;

/**
 * Interface for classification rules.
 * Rules evaluate profile data and update segment tags and scores.
 */
public interface ClassificationRule {
    String getName();
    void evaluate(Map<String, Object> profileAttributes, Segment segment);
}
