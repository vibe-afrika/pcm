package dev.vibeafrika.pcm.profile.domain.repository;

import dev.vibeafrika.pcm.profile.domain.model.Handle;
import dev.vibeafrika.pcm.profile.domain.model.Profile;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface (port) for Profile aggregate.
 * This is a domain interface - implementations belong in the infrastructure layer.
 */
public interface ProfileRepository {
    
    /**
     * Save a profile (create or update).
     */
    Profile save(Profile profile);
    
    /**
     * Find a profile by its ID.
     */
    Optional<Profile> findById(UUID id);
    
    /**
     * Find a profile by handle.
     */
    Optional<Profile> findByHandle(Handle handle);
    
    /**
     * Find a profile by ID and tenant ID.
     */
    Optional<Profile> findByIdAndTenantId(UUID id, String tenantId);
    
    /**
     * Check if a handle exists.
     */
    boolean existsByHandle(Handle handle);
    
    /**
     * Delete a profile (hard delete - use with caution).
     */
    void delete(Profile profile);
}
