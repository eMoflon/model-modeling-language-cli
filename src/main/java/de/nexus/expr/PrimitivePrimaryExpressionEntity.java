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
}
