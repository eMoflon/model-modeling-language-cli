package de.nexus.expr;

public class EnumValuePrimaryExpressionEntity implements PrimaryExpressionEntity {
    private final String enumName;
    private final String value;

    public EnumValuePrimaryExpressionEntity(String enumName, String value) {
        this.enumName = enumName;
        this.value = value;
    }

    public String getEnumName() {
        return enumName;
    }

    public String getValue() {
        return value;
    }

    @Override
    public PrimaryExpressionEntityType getType() {
        return PrimaryExpressionEntityType.ENUM_VALUE;
    }

    @Override
    public String toJavaCode() {
        throw new UnsupportedOperationException("EnumValuePrimaryExpressions cannot be transfered to the model server!");
    }

    @Override
    public String toInterpretableJavaCode() {
        return String.format("ValueWrapper.create(\"%s\")", this.value);
    }

    @Override
    public String toString() {
        return String.format("{(EnumValue | %s) %s}", this.enumName, this.value);
    }

    @Override
    public String toSimpleString() {
        return this.value;
    }
}
