package dev.vibeafrika.pcm.preference.infrastructure.persistence;

import dev.vibeafrika.pcm.preference.domain.model.Preference;
import org.springframework.data.repository.CrudRepository;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data Redis repository for Preference hash objects.
 */
public interface SpringDataPreferenceRepository extends CrudRepository<Preference, UUID> {
    Optional<Preference> findByProfileId(UUID profileId);
    Optional<Preference> findByProfileIdAndTenantId(UUID profileId, String tenantId);
}
