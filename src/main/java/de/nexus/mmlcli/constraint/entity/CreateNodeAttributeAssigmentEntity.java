package de.nexus.mmlcli.constraint.entity;

import de.nexus.expr.EnumValuePrimaryExpressionEntity;
import de.nexus.expr.PrimaryExpressionEntity;
import de.nexus.expr.PrimitivePrimaryExpressionEntity;

public class CreateNodeAttributeAssigmentEntity {
    private final String attributeName;
    private final PrimaryExpressionEntity attributeValue;

    public CreateNodeAttributeAssigmentEntity(String attributeName, PrimaryExpressionEntity attributeValue) {
        this.attributeName = attributeName;
        this.attributeValue = attributeValue;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public PrimaryExpressionEntity getAttributeValue() {
        return attributeValue;
    }

    public String toJavaCode() {
        if (this.attributeValue instanceof EnumValuePrimaryExpressionEntity enumExpr) {
            return String.format("new AttributeAssignment(\"%s\",\"%s\")", this.attributeName, enumExpr.getValue());
        } else if (this.attributeValue instanceof PrimitivePrimaryExpressionEntity<?> primitivePrimaryExpression) {
            return String.format("new AttributeAssignment(\"%s\",\"%s\")", this.attributeName, primitivePrimaryExpression.getAsString());
        } else {
            throw new UnsupportedOperationException("CreateNodeAttributeAssignments do not support PrimaryExpressions of type: " + this.attributeValue.getClass().getName());
        }
    }
}
