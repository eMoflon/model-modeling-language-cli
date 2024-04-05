package de.nexus.modelserver.evaltree;

import de.nexus.expr.BinaryExpressionEntity;
import de.nexus.expr.ValueWrapper;
import de.nexus.modelserver.AbstractConstraint;
import de.nexus.modelserver.PatternRegistry;

public class EvalTreeBiNode implements IEvalTreeNode {
    private final BinaryExpressionEntity expression;
    private final IEvalTreeNode leftChild;
    private final IEvalTreeNode rightChild;
    private final ValueWrapper<?> value;

    private EvalTreeBiNode(BinaryExpressionEntity expr, IEvalTreeNode leftChild, IEvalTreeNode rightChild, ValueWrapper<?> value) {
        this.expression = expr;
        this.leftChild = leftChild;
        this.rightChild = rightChild;
        this.value = value;
    }

    static EvalTreeBiNode evaluate(BinaryExpressionEntity expr, PatternRegistry patternRegistry, AbstractConstraint constraint) {
        IEvalTreeNode leftChild = EvalTree.evaluateSubExpr(expr.getLeft(), patternRegistry, constraint);
        IEvalTreeNode rightChild = EvalTree.evaluateSubExpr(expr.getRight(), patternRegistry, constraint);
        ValueWrapper<?> value = expr.getOperator().apply(leftChild.getValue(), rightChild.getValue());

        return new EvalTreeBiNode(expr, leftChild, rightChild, value);
    }

    @Override
    public BinaryExpressionEntity getExpression() {
        return expression;
    }

    public IEvalTreeNode getLeftChild() {
        return leftChild;
    }

    public IEvalTreeNode getRightChild() {
        return rightChild;
    }

    @Override
    public String toFormattedString(int indent) {
        String indentString = "    ".repeat(indent);
        return indentString + String.format("%s -> %s", this.expression.toString(), this.value.toString()) + "\n" + indentString + this.leftChild.toFormattedString(indent + 1) + "\n" + indentString + this.rightChild.toFormattedString(indent + 1);
    }

    @Override
    public ValueWrapper<?> getValue() {
        return this.value;
    }

}
