package de.nexus.modelserver.evaltree;

import de.nexus.mmlcli.constraint.entity.expr.UnaryExpressionEntity;
import de.nexus.modelserver.PatternRegistry;

public class EvalTreeUniNode implements IEvalTreeNode {
    private final UnaryExpressionEntity expression;
    private final IEvalTreeNode child;
    private final EvalTreeValue value;

    private EvalTreeUniNode(UnaryExpressionEntity expression, IEvalTreeNode child) {
        this.expression = expression;
        this.child = child;

        if (child instanceof EvalTreeValueBoolean boolValue) {
            this.value = new EvalTreeValueBoolean(expression.getOperator().applyBool(boolValue.getValue()));
        } else {
            throw new UnsupportedOperationException("Unsupported BoolEvalTreeValue: " + child.getClass().getName());
        }
    }

    public UnaryExpressionEntity getExpression() {
        return expression;
    }

    public IEvalTreeNode getChild() {
        return child;
    }

    public EvalTreeValue getValue() {
        return value;
    }

    static EvalTreeUniNode evaluate(UnaryExpressionEntity expr, PatternRegistry patternRegistry) {
        IEvalTreeNode node = EvalTree.evaluateSubExpr(expr.getExpr(), patternRegistry);
        return new EvalTreeUniNode(expr, node);
    }
}
