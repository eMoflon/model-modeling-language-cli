package de.nexus.modelserver.evaltree;

import de.nexus.expr.BinaryExpressionEntity;
import de.nexus.expr.BinaryOperator;
import de.nexus.modelserver.AbstractConstraint;
import de.nexus.modelserver.PatternRegistry;

public class EvalTreeBiNode implements IEvalTreeNode {
    private final BinaryExpressionEntity expr;
    private final IEvalTreeNode leftChild;
    private final IEvalTreeNode rightChild;
    private final EvalTreeValue value;

    private EvalTreeBiNode(BinaryExpressionEntity expr, IEvalTreeNode leftChild, IEvalTreeNode rightChild, EvalTreeValue value) {
        this.expr = expr;
        this.leftChild = leftChild;
        this.rightChild = rightChild;
        this.value = value;
    }

    static EvalTreeBiNode evaluate(BinaryExpressionEntity expr, PatternRegistry patternRegistry, AbstractConstraint constraint) {
        IEvalTreeNode leftChild = EvalTree.evaluateSubExpr(expr.getLeft(), patternRegistry, constraint);
        IEvalTreeNode rightChild = EvalTree.evaluateSubExpr(expr.getRight(), patternRegistry, constraint);
        EvalTreeValue value = evaluateBinaryExpr(expr.getOperator(), leftChild.getValue(), rightChild.getValue());

        return new EvalTreeBiNode(expr, leftChild, rightChild, value);
    }

    private static EvalTreeValue evaluateBinaryExpr(BinaryOperator operator, EvalTreeValue leftChild, EvalTreeValue rightChild) {
        return switch (operator) {
            case LOGICAL_AND, LOGICAL_OR -> {
                if (leftChild instanceof EvalTreeValueBoolean lVal && rightChild instanceof EvalTreeValueBoolean rVal) {
                    yield new EvalTreeValueBoolean(operator.applyBool(lVal.getValue(), rVal.getValue()));
                } else {
                    throw new UnsupportedOperationException("Unsupported child types in BoolEvalTree BinaryEvaluation!");
                }
            }
            case EQUALS, NOT_EQUALS -> {
                if (leftChild instanceof EvalTreeValueInteger lVal && rightChild instanceof EvalTreeValueInteger rVal) {
                    yield new EvalTreeValueBoolean(operator.applyBool(lVal.getValue(), rVal.getValue()));
                } else if (leftChild instanceof EvalTreeValueInteger lVal && rightChild instanceof EvalTreeValueDouble rVal) {
                    yield new EvalTreeValueBoolean(operator.applyBool(lVal.getValue(), rVal.getValue()));
                } else if (leftChild instanceof EvalTreeValueDouble lVal && rightChild instanceof EvalTreeValueInteger rVal) {
                    yield new EvalTreeValueBoolean(operator.applyBool(lVal.getValue(), rVal.getValue()));
                } else if (leftChild instanceof EvalTreeValueDouble lVal && rightChild instanceof EvalTreeValueDouble rVal) {
                    yield new EvalTreeValueBoolean(operator.applyBool(lVal.getValue(), rVal.getValue()));
                } else if (leftChild instanceof EvalTreeValueBoolean lVal && rightChild instanceof EvalTreeValueBoolean rVal) {
                    yield new EvalTreeValueBoolean(operator.applyBool(lVal.getValue(), rVal.getValue()));
                } else if (leftChild instanceof EvalTreeValueString lVal && rightChild instanceof EvalTreeValueString rVal) {
                    yield new EvalTreeValueBoolean(operator.applyBool(lVal.getValue(), rVal.getValue()));
                } else {
                    throw new UnsupportedOperationException("Unsupported child types in BoolEvalTree BinaryEvaluation!");
                }
            }
            case LESS_THAN, LESS_EQUAL_THAN, GREATER_THAN, GREATER_EQUAL_THAN -> {
                if (leftChild instanceof EvalTreeValueInteger lVal && rightChild instanceof EvalTreeValueInteger rVal) {
                    yield new EvalTreeValueBoolean(operator.applyBool(lVal.getValue(), rVal.getValue()));
                } else if (leftChild instanceof EvalTreeValueInteger lVal && rightChild instanceof EvalTreeValueDouble rVal) {
                    yield new EvalTreeValueBoolean(operator.applyBool(lVal.getValue(), rVal.getValue()));
                } else if (leftChild instanceof EvalTreeValueDouble lVal && rightChild instanceof EvalTreeValueInteger rVal) {
                    yield new EvalTreeValueBoolean(operator.applyBool(lVal.getValue(), rVal.getValue()));
                } else if (leftChild instanceof EvalTreeValueDouble lVal && rightChild instanceof EvalTreeValueDouble rVal) {
                    yield new EvalTreeValueBoolean(operator.applyBool(lVal.getValue(), rVal.getValue()));
                } else {
                    throw new UnsupportedOperationException("Unsupported child types in BoolEvalTree BinaryEvaluation!");
                }
            }
            default -> throw new UnsupportedOperationException("Unsupported binary operator!");
        };
    }

    public BinaryExpressionEntity getExpr() {
        return expr;
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
        return indentString + String.format("%s -> %s", this.expr.toString(), this.value.toString()) + "\n" + indentString + this.leftChild.toFormattedString(indent + 1) + "\n" + indentString + this.rightChild.toFormattedString(indent + 1);
    }

    @Override
    public EvalTreeValue getValue() {
        return this.value;
    }

}
