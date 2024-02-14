package de.nexus.mmlcli.constraint.entity;

import de.nexus.expr.ExpressionEntity;

public class ConstraintAssertionEntity {
    private final ExpressionEntity expr;

    public ConstraintAssertionEntity(ExpressionEntity expr) {
        this.expr = expr;
    }

    public ExpressionEntity getExpr() {
        return expr;
    }
}
