package com.gymleveling.auth.internal;

import jakarta.persistence.LockModeType;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.Instant;

interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, UUID> {

	Optional<RefreshTokenEntity> findByTokenHash(String tokenHash);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select t from RefreshTokenEntity t where t.tokenHash = :hash")
	Optional<RefreshTokenEntity> findByTokenHashForUpdate(@Param("hash") String hash);

	@Modifying
	@Query("update RefreshTokenEntity t set t.revokedAt = :now where t.userId = :userId and t.revokedAt is null")
	int revokeAllForUser(@Param("userId") UUID userId, @Param("now") Instant now);

	@Modifying
	@Query("update RefreshTokenEntity t set t.revokedAt = :now where t.familyId = :familyId and t.revokedAt is null")
	int revokeFamily(@Param("familyId") UUID familyId, @Param("now") Instant now);

}
