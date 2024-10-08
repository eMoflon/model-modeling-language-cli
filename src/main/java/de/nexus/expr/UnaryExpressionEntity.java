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
        return String.format("new UnaryExpressionEntity(%s, %s)", "UnaryOperator." + this.operator.name(), this.expr.toJavaCode());
    }

    @Override
    public String toInterpretableJavaCode() {
        return switch (this.operator) {
            case NEGATION -> String.format("%s.neg()", this.expr.toInterpretableJavaCode());
        };
    }

    @Override
    public String toString() {
        return String.format("{[%s] %s}", this.operator.name(), this.expr.toString());
    }

    @Override
    public String toSimpleString() {
        return switch (this.operator) {
            case NEGATION -> "!" + this.expr.toSimpleString();
        };
    }
}
