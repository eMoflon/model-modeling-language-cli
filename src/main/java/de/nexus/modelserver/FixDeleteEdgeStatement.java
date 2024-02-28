package de.nexus.modelserver;

public class FixDeleteEdgeStatement implements FixStatement {
    private final String fromPatternNodeName;
    private final String toPatternNodeName;
    private final String referenceName;

    public FixDeleteEdgeStatement(String fromPatternNodeName, String toPatternNodeName, String referenceName) {
        this.fromPatternNodeName = fromPatternNodeName;
        this.toPatternNodeName = toPatternNodeName;
        this.referenceName = referenceName;
    }

    public String getFromPatternNodeName() {
        return fromPatternNodeName;
    }

    public String getToPatternNodeName() {
        return toPatternNodeName;
    }

    public String getReferenceName() {
        return referenceName;
    }
}
