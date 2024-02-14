package de.nexus.mmlcli.constraint.entity;

import de.nexus.mmlcli.constraint.entity.expr.BinaryExpressionEntity;
import de.nexus.mmlcli.constraint.entity.expr.ExpressionEntity;
import de.nexus.mmlcli.constraint.entity.expr.PrimaryExpressionEntity;

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
