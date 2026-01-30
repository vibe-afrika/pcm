package dev.vibeafrika.pcm.consent.domain.service;

import dev.vibeafrika.pcm.consent.domain.model.ConsentPurpose;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain service for generating cryptographic proof hashes for consent records.
 */
public interface ProofHashGenerator {
    
    /**
     * Generates a SHA-256 hash representing the proof of consent.
     */
    String generate(UUID profileId, ConsentPurpose purpose, boolean granted, 
                    String version, String consentText, Instant timestamp, 
                    String ipAddress, String userAgent);
}
