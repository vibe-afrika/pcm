package dev.vibeafrika.pcm.preference.domain.repository;

import dev.vibeafrika.pcm.preference.domain.model.Preference;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository port for Preference persistence.
 */
public interface PreferenceRepository {
    Preference save(Preference preference);
    Optional<Preference> findByProfileId(UUID profileId);
    Optional<Preference> findByProfileIdAndTenantId(UUID profileId, String tenantId);
    void delete(Preference preference);
}
