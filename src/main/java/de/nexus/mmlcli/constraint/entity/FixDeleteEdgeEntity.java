package de.nexus.mmlcli.constraint.entity;

public class FixDeleteEdgeEntity implements IFixStatementEntity {
    private final String referenceName;
    private final String fromPatternNodeName;
    private final String toPatternNodeName;

    public FixDeleteEdgeEntity(String fromPatternNodeName, String toPatternNodeName, String referenceName) {
        this.referenceName = referenceName;
        this.fromPatternNodeName = fromPatternNodeName;
        this.toPatternNodeName = toPatternNodeName;
    }

    public String getReferenceName() {
        return referenceName;
    }

    public String getFromPatternNodeName() {
        return fromPatternNodeName;
    }

    public String getToPatternNodeName() {
        return toPatternNodeName;
    }

    @Override
    public String toJavaCode() {
        return String.format("new FixDeleteEdgeStatement(\"%s\", \"%s\", \"%s\")", this.fromPatternNodeName, this.toPatternNodeName, this.referenceName);
    }
}
