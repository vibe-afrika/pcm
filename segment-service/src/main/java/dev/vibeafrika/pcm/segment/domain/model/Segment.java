package dev.vibeafrika.pcm.segment.domain.model;

import dev.vibeafrika.pcm.common.domain.AggregateRoot;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.Instant;
import java.util.*;

/**
 * Segment aggregate root - represents a user's classification and behavioral scores.
 * Stored in Elasticsearch for fast querying and filtering.
 */
@Document(indexName = "user_segments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Segment implements AggregateRoot<UUID> {

    @Id
    private UUID id;

    @Field(type = FieldType.Keyword)
    private String tenantId;

    @Field(type = FieldType.Keyword)
    private UUID profileId;

    @Field(type = FieldType.Keyword)
    private Set<String> tags = new HashSet<>();

    @Field(type = FieldType.Object)
    private Map<String, Double> scores = new HashMap<>();

    @Field(type = FieldType.Date)
    private Instant lastUpdated;

    private Segment(String tenantId, UUID profileId) {
        this.id = UUID.randomUUID();
        this.tenantId = tenantId;
        this.profileId = profileId;
        this.lastUpdated = Instant.now();
    }

    public static Segment create(String tenantId, UUID profileId) {
        return new Segment(tenantId, profileId);
    }

    public void updateSegments(Set<String> newTags, Map<String, Double> newScores) {
        this.tags = new HashSet<>(newTags);
        this.scores = new HashMap<>(newScores);
        this.lastUpdated = Instant.now();
    }

    public void addTag(String tag) {
        this.tags.add(tag);
        this.lastUpdated = Instant.now();
    }

    public void removeTag(String tag) {
        this.tags.remove(tag);
        this.lastUpdated = Instant.now();
    }

    public void setScore(String key, Double value) {
        this.scores.put(key, value);
        this.lastUpdated = Instant.now();
    }
}
