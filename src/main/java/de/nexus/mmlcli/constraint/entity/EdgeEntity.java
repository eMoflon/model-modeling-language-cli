package de.nexus.mmlcli.constraint.entity;

public class EdgeEntity {
    private String fromId;
    private transient PatternNodeEntity fromNode;
    private String toId;
    private transient PatternNodeEntity toNode;
    private String referenceName;
    private String alias;

    public String getFromId() {
        return fromId;
    }

    public String getToId() {
        return toId;
    }

    public String getReferenceName() {
        return referenceName;
    }

    public String getAlias() {
        return alias;
    }

    public PatternNodeEntity getFromNode() {
        return fromNode;
    }

    public PatternNodeEntity getToNode() {
        return toNode;
    }

    public void setFromNode(PatternNodeEntity fromNode) {
        this.fromNode = fromNode;
    }

    public void setToNode(PatternNodeEntity toNode) {
        this.toNode = toNode;
    }
}
