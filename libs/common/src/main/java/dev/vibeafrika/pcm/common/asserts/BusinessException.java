package dev.vibeafrika.pcm.common.asserts;

public abstract class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
