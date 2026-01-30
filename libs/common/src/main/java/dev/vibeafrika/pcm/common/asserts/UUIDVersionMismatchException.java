package dev.vibeafrika.pcm.common.asserts;

public class UUIDVersionMismatchException extends RuntimeException {
    private final String field;
    private final int expectedVersion;
    private final int actualVersion;

    private UUIDVersionMismatchException(String message, String field, int expectedVersion, int actualVersion) {
        super(message);
        this.field = field;
        this.expectedVersion = expectedVersion;
        this.actualVersion = actualVersion; 
    }

    public int getActualVersion() {
        return actualVersion;
    }
    
    public String getField() {
        return field;
    }
    
    public int getExpectedVersion() {
        return expectedVersion;
    }

    public static UUIDVersionMismatchExceptionBuilder builder() {
        return new UUIDVersionMismatchExceptionBuilder();
    }

    public static class UUIDVersionMismatchExceptionBuilder {
        private String field;
        private int expectedVersion;
        private int actualVersion;

        public UUIDVersionMismatchExceptionBuilder field(String field) {
            this.field = field;
            return this;
        }

        public UUIDVersionMismatchExceptionBuilder expectedVersion(int expectedVersion) {
            this.expectedVersion = expectedVersion;
            return this;
        }

        public UUIDVersionMismatchExceptionBuilder actualVersion(int actualVersion) {
            this.actualVersion = actualVersion;
            return this;
        }

        public UUIDVersionMismatchException build() {
            return new UUIDVersionMismatchException(
                    String.format("UUID version mismatch for field '%s'. Expected: %d, Actual: %d",
                            field, expectedVersion, actualVersion),
                    field, expectedVersion, actualVersion
            );
        }
    }
}
