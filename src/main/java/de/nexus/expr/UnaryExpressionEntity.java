package de.nexus.expr;

public class UnaryExpressionEntity implements ExpressionEntity {
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

    @Override
    public String toJavaCode() {
        return String.format("new UnaryExpressionEntity(%s, %s)", this.operator, this.expr.toJavaCode());
    }
}
