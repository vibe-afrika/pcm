package dev.vibeafrika.pcm.common.asserts;

import java.util.Objects;

/**
 * Exception levée lorsqu'une valeur requise est null ou invalide.
 */
public class RequiredValueException extends IllegalArgumentException {

    private final String valueName;
    private final Object invalidValue;

    public RequiredValueException(String valueName, Object invalidValue, String message) {
        super(message);
        this.valueName = Objects.requireNonNull(valueName);
        this.invalidValue = invalidValue;
    }

    public String getValueName() {
        return valueName;
    }

    public Object getInvalidValue() {
        return invalidValue;
    }

    public static RequiredValueException forNullValue(String valueName) {
        return new RequiredValueException(
                valueName, null,
                String.format("La valeur requise '%s' ne peut pas être null.", valueName)
        );
    }

    public static RequiredValueException forEmptyValue(String valueName) {
        return new RequiredValueException(
                valueName, "",
                String.format("La valeur requise '%s' ne peut pas être vide.", valueName)
        );
    }

    public static RequiredValueException forNegativeValue(String valueName, Number invalidValue) {
        return new RequiredValueException(
                valueName, invalidValue,
                String.format("La valeur requise '%s' ne peut pas être négative. Valeur invalide : %s", valueName, invalidValue)
        );
    }

    public static RequiredValueException forInvalidValue(String valueName, Object invalidValue, String reason) {
        return new RequiredValueException(
                valueName, invalidValue,
                String.format("La valeur requise '%s' est invalide : %s. Valeur invalide : %s", valueName, reason, invalidValue)
        );
    }
}
