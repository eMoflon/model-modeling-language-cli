package de.nexus.expr;

public class PrimitivePrimaryExpressionEntity<T> implements PrimaryExpressionEntity {
    private final T value;
    private final PrimaryExpressionEntityType type;

    public PrimitivePrimaryExpressionEntity(T value, PrimaryExpressionEntityType type) {
        this.value = value;
        this.type = type;
    }

    public T getValue() {
        return this.value;
    }

    public String getAsString() {
        if (this.type == PrimaryExpressionEntityType.STRING) {
            return "\"" + this.value + "\"";
        } else {
            return String.valueOf(this.value);
        }
    }

    @Override
    public PrimaryExpressionEntityType getType() {
        return type;
    }

    @Override
    public String toJavaCode() {
        return String.format("new PrimitivePrimaryExpressionEntity(%s, %s)", this.getAsString(), this.type);
    }

    @Override
    public String toInterpretableJavaCode() {
        return switch (this.type) {
            case STRING -> String.format("ValueWrapper.create(%s)", this.getAsString());
            case BOOLEAN -> String.format("ValueWrapper.create((boolean) %b)", this.value);
            case INTEGER -> String.format("ValueWrapper.create((int) %d)", (int) this.value);
            case DOUBLE -> String.format("ValueWrapper.create((double) %f)", (double) this.value);
            case NUMBER -> String.format("ValueWrapper.create((double) %f)", Double.parseDouble(this.value.toString()));
            default ->
                    throw new IllegalArgumentException("Unable to create ValueWrapper from PrimitivePrimaryExpression for " + this.type);
        };
    }

    @Override
    public String toString() {
        return String.format("{(PrimitiveValue) %s}", this.value);
    }

    @Override
    public String toSimpleString() {
        return this.getAsString();
    }
}
