package de.nexus.modelserver.evaltree;

import de.nexus.mmlcli.constraint.entity.expr.ExpressionEntity;
import de.nexus.mmlcli.constraint.entity.expr.PatternPrimaryExpressionEntity;
import de.nexus.mmlcli.constraint.entity.expr.PrimaryExpressionEntity;
import de.nexus.mmlcli.constraint.entity.expr.PrimitivePrimaryExpressionEntity;
import de.nexus.modelserver.PatternRegistry;

public class EvalTreeLeaf implements IEvalTreeNode {
    private final ExpressionEntity expression;
    private final EvalTreeValue value;

    private EvalTreeLeaf(ExpressionEntity expression, EvalTreeValue value) {
        this.expression = expression;
        this.value = value;
    }

    public ExpressionEntity getExpression() {
        return expression;
    }

    public EvalTreeValue getValue() {
        return value;
    }

    static EvalTreeLeaf evaluate(PrimaryExpressionEntity expr, PatternRegistry patternRegistry) {
        if (expr instanceof PrimitivePrimaryExpressionEntity<?> primitiveExpr) {
            return switch (primitiveExpr.getType()) {
                case STRING ->
                        new EvalTreeLeaf(expr, new EvalTreeValueString((String) ((PrimitivePrimaryExpressionEntity<?>) expr).getValue()));
                case BOOLEAN ->
                        new EvalTreeLeaf(expr, new EvalTreeValueBoolean((boolean) ((PrimitivePrimaryExpressionEntity<?>) expr).getValue()));
                case NUMBER, DOUBLE ->
                        new EvalTreeLeaf(expr, new EvalTreeValueDouble((double) ((PrimitivePrimaryExpressionEntity<?>) expr).getValue()));
                case INTEGER ->
                        new EvalTreeLeaf(expr, new EvalTreeValueInteger((int) ((PrimitivePrimaryExpressionEntity<?>) expr).getValue()));
                default -> throw new IllegalStateException("Unexpected value: " + primitiveExpr.getType());
            };
        } else if (expr instanceof PatternPrimaryExpressionEntity patternExpr) {
            return new EvalTreeLeaf(expr, new EvalTreeValueBoolean(patternRegistry.getPattern(patternExpr.getPatternName()).hasNone()));
        } else {
            throw new UnsupportedOperationException("BoolEvalTree does currently not support: " + expr.getClass().getName());
        }
    }
}
