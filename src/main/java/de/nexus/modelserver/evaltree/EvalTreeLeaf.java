package de.nexus.modelserver.evaltree;

import de.nexus.expr.ExpressionEntity;
import de.nexus.expr.PatternPrimaryExpressionEntity;
import de.nexus.expr.PrimaryExpressionEntity;
import de.nexus.expr.PrimitivePrimaryExpressionEntity;
import de.nexus.modelserver.AbstractConstraint;
import de.nexus.modelserver.Pattern;
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

    static EvalTreeLeaf evaluate(PrimaryExpressionEntity expr, PatternRegistry patternRegistry, AbstractConstraint constraint) {
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
            String patternName = constraint.getPatternDeclarations().get(patternExpr.getPatternName()).getPatternName();
            Pattern pattern = patternRegistry.getPattern(patternName);
            return new EvalTreeLeaf(expr, new EvalTreeValueBoolean(pattern.hasAny()));
        } else {
            throw new UnsupportedOperationException("BoolEvalTree does currently not support: " + expr.getClass().getName());
        }
    }
}
