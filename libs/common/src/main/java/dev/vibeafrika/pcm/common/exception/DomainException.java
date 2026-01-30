package dev.vibeafrika.pcm.common.exception;

/**
 * Base exception for all domain exceptions in PCM.
 */
public class DomainException extends RuntimeException {
    
    public DomainException(String message) {
        super(message);
    }
    
    public DomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
