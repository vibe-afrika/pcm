package dev.vibeafrika.pcm.profile.infrastructure.persistence;

import dev.vibeafrika.pcm.profile.domain.model.Handle;
import dev.vibeafrika.pcm.profile.domain.model.Profile;
import dev.vibeafrika.pcm.profile.domain.repository.ProfileRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for Profile entities.
 */
@Repository
public interface JpaProfileRepository extends JpaRepository<Profile, UUID>, ProfileRepository {

    Optional<Profile> findByHandle(Handle handle);

    Optional<Profile> findByIdAndTenantId(UUID id, String tenantId);

    boolean existsByHandle(Handle handle);
}
