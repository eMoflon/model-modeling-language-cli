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
        return String.format("new BinaryExpressionEntity(%s, %s, %s)", this.operator, this.left.toJavaCode(), this.right.toJavaCode());
    }

    @Override
    public String toString() {
        return String.format("{%s [%s] %s}", this.left.toString(), this.operator.name(), this.right.toString());
    }
}
