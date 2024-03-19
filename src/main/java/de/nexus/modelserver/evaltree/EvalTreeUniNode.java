package de.nexus.modelserver.evaltree;

import de.nexus.expr.UnaryExpressionEntity;
import de.nexus.modelserver.AbstractConstraint;
import de.nexus.modelserver.PatternRegistry;

public class EvalTreeUniNode implements IEvalTreeNode {
    private final UnaryExpressionEntity expression;
    private final IEvalTreeNode child;
    private final EvalTreeValue value;

    private EvalTreeUniNode(UnaryExpressionEntity expression, IEvalTreeNode child) {
        this.expression = expression;
        this.child = child;

        if (child.getValue() instanceof EvalTreeValueBoolean boolValue) {
            this.value = new EvalTreeValueBoolean(expression.getOperator().applyBool(boolValue.getValue()));
        } else {
            throw new UnsupportedOperationException("Unsupported BoolEvalTreeValue: " + child.getClass().getName());
        }
    }

    @Override
    public UnaryExpressionEntity getExpression() {
        return expression;
    }

    public IEvalTreeNode getChild() {
        return child;
    }

    public EvalTreeValue getValue() {
        return value;
    }

    static EvalTreeUniNode evaluate(UnaryExpressionEntity expr, PatternRegistry patternRegistry, AbstractConstraint constraint) {
        IEvalTreeNode node = EvalTree.evaluateSubExpr(expr.getExpr(), patternRegistry, constraint);
        return new EvalTreeUniNode(expr, node);
    }

    @Override
    public String toFormattedString(int indent) {
        String indentString = "    ".repeat(indent);
        return indentString + String.format("%s -> %s", this.expression.toString(), this.value.toString()) + "\n" + indentString + child.toFormattedString(indent + 1);
    }
}
