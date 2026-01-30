package dev.vibeafrika.pcm.consent.infrastructure.persistence;

import dev.vibeafrika.pcm.consent.domain.model.Consent;
import dev.vibeafrika.pcm.consent.domain.model.ConsentPurpose;
import dev.vibeafrika.pcm.consent.domain.repository.ConsentRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA implementation of ConsentRepository.
 */
@Repository
public interface JpaConsentRepository extends JpaRepository<Consent, UUID>, ConsentRepository {

    @Override
    @Query("SELECT c FROM Consent c WHERE c.profileId = :profileId AND c.purpose = :purpose ORDER BY c.timestamp DESC LIMIT 1")
    Optional<Consent> findLatestByProfileIdAndPurpose(UUID profileId, ConsentPurpose purpose);

    @Override
    @Query("SELECT c FROM Consent c WHERE c.profileId = :profileId ORDER BY c.timestamp DESC")
    List<Consent> findHistoryByProfileId(UUID profileId);

    @Override
    @Query("SELECT c FROM Consent c WHERE c.profileId = :profileId AND c.purpose = :purpose ORDER BY c.timestamp DESC")
    List<Consent> findHistoryByProfileIdAndPurpose(UUID profileId, ConsentPurpose purpose);
}
