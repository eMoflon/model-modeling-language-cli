package de.nexus.modelserver.evaltree;

import de.nexus.expr.ExpressionEntity;
import de.nexus.expr.ValueWrapper;

public interface IEvalTreeNode {
    String toFormattedString(int indent);

    ValueWrapper<?> getValue();

    ExpressionEntity getExpression();
}
