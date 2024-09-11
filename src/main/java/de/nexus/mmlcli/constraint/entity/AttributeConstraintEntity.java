package de.nexus.mmlcli.constraint.entity;

import de.nexus.expr.BinaryExpressionEntity;
import de.nexus.expr.ExpressionEntity;
import de.nexus.expr.PrimaryExpressionEntity;

public class AttributeConstraintEntity {
    private final ExpressionEntity expr;
    private String alias;

    public AttributeConstraintEntity(ExpressionEntity expr) {
        this.expr = expr;
    }

    public boolean isRelationalConstraint() {
        if (expr instanceof BinaryExpressionEntity bexpr) {
            return bexpr.getLeft() instanceof PrimaryExpressionEntity && bexpr.getRight() instanceof PrimaryExpressionEntity;
        }
        return false;
    }

    public boolean isComplexConstraint() {
        return !isRelationalConstraint();
    }

    public ExpressionEntity getExpr() {
        return expr;
    }

    public String getAlias() {
        return alias;
    }
}
