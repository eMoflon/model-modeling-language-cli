package de.nexus.expr;

public class EnumValuePrimaryExpressionEntity implements PrimaryExpressionEntity {
    private final String value;

    public EnumValuePrimaryExpressionEntity(String value) {
        this.value = value;
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
}
