package de.nexus.expr;

public class AttributePrimaryExpressionEntity implements PrimaryExpressionEntity {
    private final String className;
    private final String elementName;
    private final String nodeId;

    public AttributePrimaryExpressionEntity(String className, String elementName, String nodeId) {
        this.className = className;
        this.elementName = elementName;
        this.nodeId = nodeId;
    }

    public String getClassName() {
        return className;
    }

    public String getElementName() {
        return elementName;
    }

    public String getNodeId() {
        return nodeId;
    }

    @Override
    public PrimaryExpressionEntityType getType() {
        return PrimaryExpressionEntityType.ATTRIBUTE;
    }

    @Override
    public String toJavaCode() {
        throw new UnsupportedOperationException("AttributePrimaryExpressions cannot be transfered to the model server!");
    }

    @Override
    public String toString() {
        return String.format("{(AttributeValue) %s -> %s}", this.className, this.elementName);
    }
}
