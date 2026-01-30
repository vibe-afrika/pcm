package dev.vibeafrika.pcm.consent.domain.repository;

import dev.vibeafrika.pcm.consent.domain.model.Consent;
import dev.vibeafrika.pcm.consent.domain.model.ConsentPurpose;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository port for Consent instances.
 * Focuses on immutable ledger operations (append-only).
 */
public interface ConsentRepository {
    
    /**
     * Appends a new consent entry to the ledger.
     */
    Consent save(Consent consent);
    
    /**
     * Finds the latest consent record for a profile and purpose.
     */
    Optional<Consent> findLatestByProfileIdAndPurpose(UUID profileId, ConsentPurpose purpose);
    
    /**
     * Retrieves the full consent history for a profile.
     */
    List<Consent> findHistoryByProfileId(UUID profileId);
    
    /**
     * Retrieves the history for a specific purpose for a profile.
     */
    List<Consent> findHistoryByProfileIdAndPurpose(UUID profileId, ConsentPurpose purpose);
}
