package dev.vibeafrika.pcm.common.asserts;

public class UUIDIsNilException extends RuntimeException{
    private final String field;

    public String getField() {
        return field;
    }

    private UUIDIsNilException(String message, String field) {
        super(message);
        this.field = field;
    }

    public static UUIDIsNilExceptionBuilder builder() {
        return new UUIDIsNilExceptionBuilder();
    }

    public static class UUIDIsNilExceptionBuilder {
        private String field;

        public UUIDIsNilExceptionBuilder field(String field) {
            this.field = field;
            return this;
        }

        public UUIDIsNilException build() {
            return new UUIDIsNilException(
                    String.format("UUID for field '%s' is nil (00000000-0000-0000-0000-000000000000)", field),
                    field
            );
        }
    }
}