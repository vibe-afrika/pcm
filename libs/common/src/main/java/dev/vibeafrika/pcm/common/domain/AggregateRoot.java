package dev.vibeafrika.pcm.common.domain;

/**
 * Base interface for all aggregate roots.
 * 
 * @param <ID> The type of the aggregate identifier.
 */
public interface AggregateRoot<ID> {
    ID getId();
}
