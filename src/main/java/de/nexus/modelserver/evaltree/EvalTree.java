package de.nexus.modelserver.evaltree;

import de.nexus.mmlcli.constraint.entity.expr.BinaryExpressionEntity;
import de.nexus.mmlcli.constraint.entity.expr.ExpressionEntity;
import de.nexus.mmlcli.constraint.entity.expr.PrimaryExpressionEntity;
import de.nexus.mmlcli.constraint.entity.expr.UnaryExpressionEntity;
import de.nexus.modelserver.PatternRegistry;

public class EvalTree {
    private final IEvalTreeNode root;

    private EvalTree(IEvalTreeNode root) {
        this.root = root;
    }

    public IEvalTreeNode getRoot() {
        return root;
    }

    public static EvalTree build(ExpressionEntity expr, PatternRegistry patternRegistry) {
        return new EvalTree(evaluateSubExpr(expr, patternRegistry));
    }

    static IEvalTreeNode evaluateSubExpr(ExpressionEntity expr, PatternRegistry patternRegistry) {
        if (expr instanceof BinaryExpressionEntity biExpr) {
            return EvalTreeBiNode.evaluate(biExpr, patternRegistry);
        } else if (expr instanceof UnaryExpressionEntity unaryExpr) {
            return EvalTreeUniNode.evaluate(unaryExpr, patternRegistry);
        } else if (expr instanceof PrimaryExpressionEntity primaryExpr) {
            return EvalTreeLeaf.evaluate(primaryExpr, patternRegistry);
        }
        throw new RuntimeException("Unexpected ExpressionEntity of type: " + expr.getClass().getName());
    }
}
