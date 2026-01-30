# ADR 001: Hexagonal Architecture (Ports and Adapters)

## Status
Accepted

## Context
PCM is designed as a core underlying platform for multiple social and privacy-centric applications. It needs to be highly maintainable, testable, and decoupled from specific infrastructure choices (like switching from Redis to another cache, or changing the gRPC client).

## Decision
We adopt **Hexagonal Architecture** for all microservices. Every service is divided into three distinct layers:
1. **Domain**: Pure business logic (Aggregates, Value Objects, Domain Events). No dependencies on external frameworks.
2. **Application**: Use Cases that coordinate domain entities and handle DTO mapping.
3. **Infrastructure**: Adapters for databases (JPA/Hibernate), messaging (Kafka), and external clients (gRPC).

## Consequences
- **Pros**: Domain logic is protected from technological changes. Unit testing is much easier without mocks of heavy frameworks.
- **Cons**: Requires more boilerplate code (mapping between Domain objects and DTOs/Entities). Higher learning curve for new developers.
