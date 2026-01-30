package dev.vibeafrika.pcm.preference.infrastructure.persistence;

import dev.vibeafrika.pcm.preference.domain.model.Preference;
import dev.vibeafrika.pcm.preference.domain.repository.PreferenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Redis implementation of PreferenceRepository.
 */
@Repository
@RequiredArgsConstructor
public class RedisPreferenceRepositoryAdapter implements PreferenceRepository {

    private final SpringDataPreferenceRepository repository;

    @Override
    public Preference save(Preference preference) {
        return repository.save(preference);
    }

    @Override
    public Optional<Preference> findByProfileId(UUID profileId) {
        return repository.findByProfileId(profileId);
    }

    @Override
    public Optional<Preference> findByProfileIdAndTenantId(UUID profileId, String tenantId) {
        return repository.findByProfileIdAndTenantId(profileId, tenantId);
    }

    @Override
    public void delete(Preference preference) {
        repository.delete(preference);
    }
}
