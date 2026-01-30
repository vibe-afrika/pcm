package dev.vibeafrika.pcm.consent.infrastructure.security;

import dev.vibeafrika.pcm.consent.domain.model.ConsentPurpose;
import dev.vibeafrika.pcm.consent.domain.service.ProofHashGenerator;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;
import java.util.UUID;

/**
 * SHA-256 implementation of ProofHashGenerator.
 */
@Component
public class Sha256ProofHashGenerator implements ProofHashGenerator {

    @Override
    public String generate(UUID profileId, ConsentPurpose purpose, boolean granted, 
                          String version, String consentText, Instant timestamp, 
                          String ipAddress, String userAgent) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            
            String context = String.format("%s|%s|%b|%s|%s|%d|%s|%s",
                profileId.toString(),
                purpose.name(),
                granted,
                version,
                consentText,
                timestamp.toEpochMilli(),
                ipAddress != null ? ipAddress : "unknown",
                userAgent != null ? userAgent : "unknown"
            );

            byte[] hash = digest.digest(context.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }
}
