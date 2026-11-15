package com.gymleveling.auth.internal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "refresh_tokens")
class RefreshTokenEntity {

	@Id
	@GeneratedValue
	@UuidGenerator
	private UUID id;

	@Column(name = "user_id", nullable = false)
	private UUID userId;

	@JdbcTypeCode(SqlTypes.CHAR)
	@Column(name = "token_hash", nullable = false, length = 64)
	private String tokenHash;

	@Column(name = "family_id", nullable = false)
	private UUID familyId;

	@Column(name = "expires_at", nullable = false)
	private Instant expiresAt;

	@Column(name = "used_at")
	private Instant usedAt;

	@Column(name = "revoked_at")
	private Instant revokedAt;

	@Column(name = "created_at", nullable = false)
	private Instant createdAt;

	protected RefreshTokenEntity() {
	}

	RefreshTokenEntity(UUID userId, String tokenHash, UUID familyId, Instant expiresAt, Instant createdAt) {
		this.userId = userId;
		this.tokenHash = tokenHash;
		this.familyId = familyId;
		this.expiresAt = expiresAt;
		this.createdAt = createdAt;
	}

	UUID getId() { return id; }
	UUID getUserId() { return userId; }
	String getTokenHash() { return tokenHash; }
	UUID getFamilyId() { return familyId; }
	Instant getExpiresAt() { return expiresAt; }
	Instant getUsedAt() { return usedAt; }
	Instant getRevokedAt() { return revokedAt; }
	Instant getCreatedAt() { return createdAt; }
	void setUsedAt(Instant usedAt) { this.usedAt = usedAt; }
	void setRevokedAt(Instant revokedAt) { this.revokedAt = revokedAt; }

}
