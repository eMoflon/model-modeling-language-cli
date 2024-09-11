package de.nexus.modelserver;

public class AttributeAssignment {
    private final String attributeName;
    private final String attributeValue;

    public AttributeAssignment(String attributeName, String attributeValue) {
        this.attributeName = attributeName;
        this.attributeValue = attributeValue;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public String getAttributeValue() {
        return attributeValue;
    }
}
