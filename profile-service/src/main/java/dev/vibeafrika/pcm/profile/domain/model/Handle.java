package dev.vibeafrika.pcm.profile.domain.model;

import dev.vibeafrika.pcm.common.domain.ValueObject;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.regex.Pattern;

/**
 * Value Object representing a user handle (e.g., @username).
 * Handles must be unique across the system.
 */
@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Handle implements ValueObject {

    private static final Pattern HANDLE_PATTERN = Pattern.compile("^[a-z0-9_]{3,30}$");
    private static final String ANONYMIZED_PREFIX = "deleted_user_";

    @Column(name = "handle", nullable = false, unique = true, length = 50)
    private String value;

    private Handle(String value) {
        this.value = value;
    }

    /**
     * Create a new Handle from a string value.
     * @param value The handle value (without @ symbol)
     * @return A validated Handle instance
     * @throws IllegalArgumentException if the handle is invalid
     */
    public static Handle of(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Handle cannot be null or blank");
        }
        
        String normalized = value.toLowerCase().trim();
        
        if (!HANDLE_PATTERN.matcher(normalized).matches()) {
            throw new IllegalArgumentException(
                "Handle must be 3-30 characters long and contain only lowercase letters, numbers, and underscores"
            );
        }
        
        return new Handle(normalized);
    }

    /**
     * Create an anonymized handle for deleted profiles.
     * @return An anonymized Handle
     */
    public static Handle anonymized() {
        return new Handle(ANONYMIZED_PREFIX + java.util.UUID.randomUUID().toString().substring(0, 8));
    }

    /**
     * Check if this handle is anonymized.
     * @return true if anonymized, false otherwise
     */
    public boolean isAnonymized() {
        return value.startsWith(ANONYMIZED_PREFIX);
    }

    /**
     * Get the handle with @ prefix for display.
     * @return Handle with @ prefix
     */
    public String toDisplayString() {
        return "@" + value;
    }

    @Override
    public String toString() {
        return value;
    }
}
