package de.nexus.modelserver.evaltree;

import de.nexus.expr.*;
import de.nexus.modelserver.AbstractConstraint;
import de.nexus.modelserver.Pattern;
import de.nexus.modelserver.PatternRegistry;

public class EvalTreeLeaf implements IEvalTreeNode {
    private final ExpressionEntity expression;
    private final ValueWrapper<?> value;

    private EvalTreeLeaf(ExpressionEntity expression, ValueWrapper<?> value) {
        this.expression = expression;
        this.value = value;
    }

    @Override
    public ExpressionEntity getExpression() {
        return expression;
    }

    public ValueWrapper<?> getValue() {
        return value;
    }

    static EvalTreeLeaf evaluate(PrimaryExpressionEntity expr, PatternRegistry patternRegistry, AbstractConstraint constraint) {
        if (expr instanceof PrimitivePrimaryExpressionEntity<?> primitiveExpr) {
            return switch (primitiveExpr.getType()) {
                case STRING ->
                        new EvalTreeLeaf(expr, ValueWrapper.create((String) ((PrimitivePrimaryExpressionEntity<?>) expr).getValue()));
                case BOOLEAN ->
                        new EvalTreeLeaf(expr, ValueWrapper.create((boolean) ((PrimitivePrimaryExpressionEntity<?>) expr).getValue()));
                case NUMBER, DOUBLE ->
                        new EvalTreeLeaf(expr, ValueWrapper.create((double) ((PrimitivePrimaryExpressionEntity<?>) expr).getValue()));
                case INTEGER ->
                        new EvalTreeLeaf(expr, ValueWrapper.create((int) ((PrimitivePrimaryExpressionEntity<?>) expr).getValue()));
                default -> throw new IllegalStateException("Unexpected value: " + primitiveExpr.getType());
            };
        } else if (expr instanceof PatternPrimaryExpressionEntity patternExpr) {
            Pattern pattern = constraint.getPattern(patternExpr.getPatternName());
            return new EvalTreeLeaf(expr, ValueWrapper.create(pattern.hasAny()));
        } else {
            throw new UnsupportedOperationException("BoolEvalTree does currently not support: " + expr.getClass().getName());
        }
    }

    @Override
    public String toFormattedString(int indent) {
        return "    ".repeat(indent) + String.format("%s -> %s", this.expression.toString(), this.value.toString());
    }
}
