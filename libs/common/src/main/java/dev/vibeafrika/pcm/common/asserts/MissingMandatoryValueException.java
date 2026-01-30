package dev.vibeafrika.pcm.common.asserts;

public class MissingMandatoryValueException extends AssertionException {

    private MissingMandatoryValueException(final String field, final
    String message) {
        super(field, message);
    }

    public static MissingMandatoryValueException forBlankValue(String field) {
        return new MissingMandatoryValueException(field, defaultMessage(field, "blank"));
    }

    public static MissingMandatoryValueException forNullValue(String field) {
        return new MissingMandatoryValueException(field, defaultMessage(field, "null"));
    }

    public static MissingMandatoryValueException forEmptyValue(String field) {
        return new MissingMandatoryValueException(field, defaultMessage(field, "empty"));
    }

    public static MissingMandatoryValueException forBadValue(String field, String message) {
        return new MissingMandatoryValueException(field, message);
    }

    private static String defaultMessage(String field, String reason) {
        return new StringBuilder()
                .append("The field \"")
                .append(field)
                .append("\" is mandatory and wasn't set")
                .append(" (")
                .append(reason)
                .append(")")
                .toString();
    }

    @Override
    public AssertionErrorType type() {
        return AssertionErrorType.MISSING_MANDATORY_VALUE;
    }
}
