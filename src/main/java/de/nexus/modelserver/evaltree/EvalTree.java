package de.nexus.modelserver.evaltree;

import de.nexus.expr.BinaryExpressionEntity;
import de.nexus.expr.ExpressionEntity;
import de.nexus.expr.PrimaryExpressionEntity;
import de.nexus.expr.UnaryExpressionEntity;
import de.nexus.modelserver.AbstractConstraint;
import de.nexus.modelserver.PatternRegistry;

public class EvalTree {
    private final IEvalTreeNode root;

    private EvalTree(IEvalTreeNode root) {
        this.root = root;
    }

    public IEvalTreeNode getRoot() {
        return root;
    }

    public static EvalTree build(ExpressionEntity expr, PatternRegistry patternRegistry, AbstractConstraint constraint) {
        return new EvalTree(evaluateSubExpr(expr, patternRegistry, constraint));
    }

    static IEvalTreeNode evaluateSubExpr(ExpressionEntity expr, PatternRegistry patternRegistry, AbstractConstraint constraint) {
        if (expr instanceof BinaryExpressionEntity biExpr) {
            return EvalTreeBiNode.evaluate(biExpr, patternRegistry, constraint);
        } else if (expr instanceof UnaryExpressionEntity unaryExpr) {
            return EvalTreeUniNode.evaluate(unaryExpr, patternRegistry, constraint);
        } else if (expr instanceof PrimaryExpressionEntity primaryExpr) {
            return EvalTreeLeaf.evaluate(primaryExpr, patternRegistry, constraint);
        }
        throw new RuntimeException("Unexpected ExpressionEntity of type: " + expr.getClass().getName());
    }

    public boolean getState() {
        if (this.root.getValue() instanceof EvalTreeValueBoolean boolValueNode) {
            return boolValueNode.getValue();
        }
        throw new IllegalStateException("EvalTree root does not yield boolean value!");
    }

    @Override
    public String toString() {
        return "EvalTree\n" + this.root.toFormattedString(0);
    }
}
