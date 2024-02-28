package de.nexus.mmlcli.constraint.entity;

public class FixDeleteNodeEntity implements IFixStatementEntity {
    private final String nodeAlias;

    public FixDeleteNodeEntity(String nodeAlias) {
        this.nodeAlias = nodeAlias;
    }

    public String getNodeAlias() {
        return nodeAlias;
    }

    @Override
    public String toJavaCode() {
        return String.format("new FixDeleteNodeStatement(\"%s\")", this.nodeAlias);
    }
}
