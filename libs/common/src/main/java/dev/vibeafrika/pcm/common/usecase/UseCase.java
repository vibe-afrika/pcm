package dev.vibeafrika.pcm.common.usecase;

/**
 * Base interface for all use cases in the application layer.
 * Use cases represent application-specific business rules.
 * 
 * @param <I> Input type (Command or Query)
 * @param <O> Output type (Result)
 */
public interface UseCase<I, O> {
    
    /**
     * Execute the use case with the given input.
     * 
     * @param input The input command or query
     * @return The result of the use case execution
     */
    O execute(I input);
}
