package de.nexus.expr;

public class BinaryExpressionEntity implements ExpressionEntity {
    private final BinaryOperator operator;
    private final ExpressionEntity left;
    private final ExpressionEntity right;

    public BinaryExpressionEntity(BinaryOperator operator, ExpressionEntity left, ExpressionEntity right) {
        this.operator = operator;
        this.left = left;
        this.right = right;
    }

    public BinaryOperator getOperator() {
        return operator;
    }

    public ExpressionEntity getLeft() {
        return left;
    }

    public ExpressionEntity getRight() {
        return right;
    }

    @Override
    public String toJavaCode() {
        return String.format("new BinaryExpressionEntity(%s, %s, %s)", "BinaryOperator." + this.operator.name(), this.left.toJavaCode(), this.right.toJavaCode());
    }

    @Override
    public String toString() {
        return String.format("{%s [%s] %s}", this.left.toString(), this.operator.name(), this.right.toString());
    }

    @Override
    public String toSimpleString() {
        return switch (this.operator) {
            case LOGICAL_AND -> String.format("(%s && %s)", this.left.toSimpleString(), this.right.toSimpleString());
            case LOGICAL_OR -> String.format("(%s || %s)", this.left.toSimpleString(), this.right.toSimpleString());
            case ADDITION -> String.format("(%s + %s)", this.left.toSimpleString(), this.right.toSimpleString());
            case SUBTRACTION -> String.format("(%s - %s)", this.left.toSimpleString(), this.right.toSimpleString());
            case MULTIPLICATION -> String.format("(%s * %s)", this.left.toSimpleString(), this.right.toSimpleString());
            case DIVISION -> String.format("(%s / %s)", this.left.toSimpleString(), this.right.toSimpleString());
            case MODULO -> String.format("(%s %% %s)", this.left.toSimpleString(), this.right.toSimpleString());
            case EXPONENTIATION -> String.format("(%s  %s)", this.left.toSimpleString(), this.right.toSimpleString());
            case EQUALS -> String.format("(%s == %s)", this.left.toSimpleString(), this.right.toSimpleString());
            case NOT_EQUALS -> String.format("(%s != %s)", this.left.toSimpleString(), this.right.toSimpleString());
            case GREATER_THAN -> String.format("(%s > %s)", this.left.toSimpleString(), this.right.toSimpleString());
            case GREATER_EQUAL_THAN ->
                    String.format("(%s >= %s)", this.left.toSimpleString(), this.right.toSimpleString());
            case LESS_THAN -> String.format("(%s < %s)", this.left.toSimpleString(), this.right.toSimpleString());
            case LESS_EQUAL_THAN ->
                    String.format("(%s <= %s)", this.left.toSimpleString(), this.right.toSimpleString());
        };
    }
}
