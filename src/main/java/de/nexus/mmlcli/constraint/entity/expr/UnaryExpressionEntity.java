package de.nexus.mmlcli.constraint.entity.expr;

public class UnaryExpressionEntity {
    private final UnaryOperator operator;
    private final ExpressionEntity expr;

    public UnaryExpressionEntity(UnaryOperator operator, ExpressionEntity expr) {
        this.operator = operator;
        this.expr = expr;
    }

    public UnaryOperator getOperator() {
        return operator;
    }

    public ExpressionEntity getExpr() {
        return expr;
    }
}
