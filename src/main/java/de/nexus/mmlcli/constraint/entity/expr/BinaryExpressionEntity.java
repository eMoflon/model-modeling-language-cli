package de.nexus.mmlcli.constraint.entity.expr;

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
}
