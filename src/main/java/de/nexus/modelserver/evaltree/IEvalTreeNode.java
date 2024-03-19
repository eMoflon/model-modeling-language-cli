package de.nexus.modelserver.evaltree;

import de.nexus.expr.ExpressionEntity;

public interface IEvalTreeNode {
    String toFormattedString(int indent);

    EvalTreeValue getValue();

    ExpressionEntity getExpression();
}
