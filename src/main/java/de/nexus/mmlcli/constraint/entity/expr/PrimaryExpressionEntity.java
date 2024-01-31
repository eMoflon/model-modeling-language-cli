package de.nexus.mmlcli.constraint.entity.expr;

public class PrimaryExpressionEntity<T> implements ExpressionEntity {
    private final T value;
    private final PrimaryExpressionEntityType type;
    private final String className;
    private final String elementName;
    private final String nodeId;

    public PrimaryExpressionEntity(T value, PrimaryExpressionEntityType type, String className, String elementName, String nodeId) {
        this.value = value;
        this.type = type;
        this.className = className;
        this.elementName = elementName;
        this.nodeId = nodeId;
    }

    public PrimaryExpressionEntityType getType() {
        return type;
    }

    public boolean isConstantValue() {
        return this.type == PrimaryExpressionEntityType.STRING ||
                this.type == PrimaryExpressionEntityType.BOOLEAN ||
                this.type == PrimaryExpressionEntityType.INTEGER ||
                this.type == PrimaryExpressionEntityType.DOUBLE ||
                this.type == PrimaryExpressionEntityType.NUMBER;
    }

    public T getValue() {
        return value;
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
}
