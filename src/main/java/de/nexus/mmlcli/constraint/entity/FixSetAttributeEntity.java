package de.nexus.mmlcli.constraint.entity;

import de.nexus.expr.ExpressionEntity;

public class FixSetAttributeEntity implements IFixStatementEntity {
    private final String patternNodeName;
    private final String attributeName;
    private final boolean customizationRequired;
    private final ExpressionEntity attributeValue;

    public FixSetAttributeEntity(String patternNodeName, String attributeName, boolean customizationRequired, ExpressionEntity attributeValue) {
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

    public ExpressionEntity getAttributeValue() {
        return attributeValue;
    }

    @Override
    public String toJavaCode() {
        return String.format("new FixSetStatement(\"%s\", \"%s\", %b, (match) -> %s.getAsString())", this.patternNodeName, this.attributeName, this.customizationRequired, attributeValue.toInterpretableJavaCode());
    }
}
