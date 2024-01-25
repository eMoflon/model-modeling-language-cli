package de.nexus.mmlcli.constraint.entity;

public class PatternNodeEntity {
    private String nodeId;
    private String name;
    private String fqname;
    private boolean local;

    public String getNodeId() {
        return nodeId;
    }

    public String getName() {
        return name;
    }

    public String getFQName() {
        return fqname;
    }

    public boolean isLocal() {
        return local;
    }
}
