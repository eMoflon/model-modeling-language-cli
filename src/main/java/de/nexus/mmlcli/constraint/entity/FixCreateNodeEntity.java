package de.nexus.mmlcli.constraint.entity;

import java.util.List;
import java.util.stream.Collectors;

public class FixCreateNodeEntity implements IFixStatementEntity {
    private final String tempNodeName;
    private final String nodeType;
    private final List<CreateNodeAttributeAssigmentEntity> attributeAssignments;

    public FixCreateNodeEntity(String tempNodeName, String nodeType, List<CreateNodeAttributeAssigmentEntity> assignments) {
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

    public List<CreateNodeAttributeAssigmentEntity> getAttributeAssignments() {
        return attributeAssignments;
    }

    @Override
    public String toJavaCode() {
        String assignmentCreationCode = this.attributeAssignments.stream().map(CreateNodeAttributeAssigmentEntity::toJavaCode).collect(Collectors.joining(", "));
        return String.format("new FixCreateNodeStatement(\"%s\", \"%s\", List.of(%s))", this.tempNodeName, this.nodeType, assignmentCreationCode);
    }
}
