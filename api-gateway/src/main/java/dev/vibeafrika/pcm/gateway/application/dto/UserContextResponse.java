package dev.vibeafrika.pcm.gateway.application.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class UserContextResponse {
    private ProfileView profile;
    private PreferenceView preferences;
    private List<ConsentView> consents;

    @Data
    @Builder
    public static class ProfileView {
        private String id;
        private String handle;
        private Map<String, String> attributes;
    }

    @Data
    @Builder
    public static class PreferenceView {
        private String language;
        private String theme;
        private Map<String, Boolean> notifications;
    }

    @Data
    @Builder
    public static class ConsentView {
        private String purpose;
        private boolean granted;
        private String version;
        private long timestamp;
    }
}
