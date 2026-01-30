package dev.vibeafrika.pcm.segment.domain.service;

import dev.vibeafrika.pcm.segment.domain.model.Segment;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Example rule: Classifies users as "High Potential" if they have a certain score or attribute.
 */
@Component
public class EngagementRule implements ClassificationRule {

    @Override
    public String getName() {
        return "EngagementRule";
    }

    @Override
    public void evaluate(Map<String, Object> profileAttributes, Segment segment) {
        // Example: If user has a 'postCount' > 10, add 'ACTIVE_CREATOR' tag
        Object postCount = profileAttributes.get("postCount");
        if (postCount instanceof Number && ((Number) postCount).intValue() > 10) {
            segment.addTag("ACTIVE_CREATOR");
            segment.setScore("engagement", 0.85);
        } else {
            segment.setScore("engagement", 0.30);
        }
    }
}
