package dev.vibeafrika.pcm.profile.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Data Transfer Object for creating a new profile.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateProfileCommand {
    private String id;
    private String handle;
    private Map<String, Object> attributes;
}
