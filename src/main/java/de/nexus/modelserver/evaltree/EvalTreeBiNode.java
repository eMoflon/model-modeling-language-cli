package de.nexus.modelserver.evaltree;

import de.nexus.mmlcli.constraint.entity.expr.BinaryExpressionEntity;
import de.nexus.mmlcli.constraint.entity.expr.BinaryOperator;
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

    static EvalTreeBiNode evaluate(BinaryExpressionEntity expr, PatternRegistry patternRegistry) {
        IEvalTreeNode leftChild = EvalTree.evaluateSubExpr(expr.getLeft(), patternRegistry);
        IEvalTreeNode rightChild = EvalTree.evaluateSubExpr(expr.getRight(), patternRegistry);
        EvalTreeValue value = evaluateBinaryExpr(expr.getOperator(), leftChild, rightChild);

        return new EvalTreeBiNode(expr, leftChild, rightChild, value);
    }

    private static EvalTreeValue evaluateBinaryExpr(BinaryOperator operator, IEvalTreeNode leftChild, IEvalTreeNode rightChild) {
        if (leftChild instanceof EvalTreeValueInteger lVal && rightChild instanceof EvalTreeValueInteger rVal) {
            return new EvalTreeValueDouble(operator.applyDouble(lVal.getValue(), rVal.getValue()));
        } else if (leftChild instanceof EvalTreeValueInteger lVal && rightChild instanceof EvalTreeValueDouble rVal) {
            return new EvalTreeValueDouble(operator.applyDouble(lVal.getValue(), rVal.getValue()));
        } else if (leftChild instanceof EvalTreeValueInteger lVal && rightChild instanceof EvalTreeValueString rVal) {
            return new EvalTreeValueString(operator.applyString(lVal.getValue(), rVal.getValue()));
        } else if (leftChild instanceof EvalTreeValueDouble lVal && rightChild instanceof EvalTreeValueInteger rVal) {
            return new EvalTreeValueDouble(operator.applyDouble(lVal.getValue(), rVal.getValue()));
        } else if (leftChild instanceof EvalTreeValueDouble lVal && rightChild instanceof EvalTreeValueDouble rVal) {
            return new EvalTreeValueDouble(operator.applyDouble(lVal.getValue(), rVal.getValue()));
        } else if (leftChild instanceof EvalTreeValueBoolean lVal && rightChild instanceof EvalTreeValueBoolean rVal) {
            return new EvalTreeValueBoolean(operator.applyBool(lVal.getValue(), rVal.getValue()));
        } else if (leftChild instanceof EvalTreeValueString lVal && rightChild instanceof EvalTreeValueInteger rVal) {
            return new EvalTreeValueString(operator.applyString(lVal.getValue(), rVal.getValue()));
        } else if (leftChild instanceof EvalTreeValueString lVal && rightChild instanceof EvalTreeValueString rVal) {
            return new EvalTreeValueString(operator.applyString(lVal.getValue(), rVal.getValue()));
        } else {
            throw new UnsupportedOperationException("Unsupported child types in BoolEvalTree BinaryEvaluation!");
        }
    }


}
