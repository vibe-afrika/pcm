# ADR 002: Soft Delete with Auditability

## Status
Accepted

## Context
Under GDPR, users have the "Right to be Forgotten" (Erasure). However, physical deletion of records can break referential integrity in distributed logs and audit trails. We need a balance between data minimization and system stability.

## Decision
We implement a **Soft Delete** pattern using a `deletedAt` timestamp instead of a boolean flag.
- **Implementation**: We use Hibernate's `@SQLDelete` to automatically convert `repository.delete()` calls into updates, and `@Where` to filter deleted records from default queries.
- **Erasure Flow**: When a profile is "erased", we clear all PII attributes, anonymize the handle, and set the `deletedAt` timestamp.
- **Events**: A `ProfileErased` event is published to Kafka to trigger cleanup in downstream services (Consent, Preference, Segment).

## Consequences
- **Pros**: Maintains database integrity while respecting user privacy. Provides a precise audit trail of when the erasure occurred.
- **Cons**: Requires manual handling in complex native SQL queries where `@Where` might not apply. Requires careful index management (`WHERE deleted_at IS NULL`).
