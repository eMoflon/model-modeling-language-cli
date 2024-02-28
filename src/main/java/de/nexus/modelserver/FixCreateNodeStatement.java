package de.nexus.modelserver;

import java.util.List;

public class FixCreateNodeStatement implements FixStatement {
    private final String tempNodeName;
    private final String nodeType;
    private final List<AttributeAssignment> attributeAssignments;

    public FixCreateNodeStatement(String tempNodeName, String nodeType, List<AttributeAssignment> assignments) {
        this.tempNodeName = tempNodeName;
        this.nodeType = nodeType;
        this.attributeAssignments = assignments;
    }

    public String getTempNodeName() {
        return tempNodeName;
    }

    public String getNodeType() {
        return nodeType;
    }

    public List<AttributeAssignment> getAttributeAssignments() {
        return attributeAssignments;
    }
}
