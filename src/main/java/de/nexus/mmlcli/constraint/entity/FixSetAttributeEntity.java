package de.nexus.mmlcli.constraint.entity;

import de.nexus.expr.EnumValuePrimaryExpressionEntity;
import de.nexus.expr.PrimaryExpressionEntity;
import de.nexus.expr.PrimitivePrimaryExpressionEntity;

public class FixSetAttributeEntity implements IFixStatementEntity {
    private final String patternNodeName;
    private final String attributeName;
    private final boolean customizationRequired;
    private final PrimaryExpressionEntity attributeValue;

    public FixSetAttributeEntity(String patternNodeName, String attributeName, boolean customizationRequired, PrimaryExpressionEntity attributeValue) {
        this.patternNodeName = patternNodeName;
        this.attributeName = attributeName;
        this.customizationRequired = customizationRequired;
        this.attributeValue = attributeValue;
    }

    public String getPatternNodeName() {
        return patternNodeName;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public boolean isCustomizationRequired() {
        return customizationRequired;
    }

    public PrimaryExpressionEntity getAttributeValue() {
        return attributeValue;
    }

    @Override
    public String toJavaCode() {
        String attributeValue = "";
        if (!this.customizationRequired) {
            if (this.attributeValue instanceof PrimitivePrimaryExpressionEntity<?> primitiveValue) {
                attributeValue = primitiveValue.getAsString();
            } else if (this.attributeValue instanceof EnumValuePrimaryExpressionEntity enumValue) {
                attributeValue = enumValue.getValue();
            }
        }
        return String.format("new FixSetStatement(\"%s\", \"%s\", %b, \"%s\")", this.patternNodeName, this.attributeName, this.customizationRequired, attributeValue);
    }
}
