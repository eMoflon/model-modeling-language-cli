package de.nexus.modelserver;

public class FixCreateEdgeStatement implements FixStatement {
    private final String fromPatternNodeName;
    private final boolean fromNameIsTemp;
    private final String toPatternNodeName;
    private final boolean toNameIsTemp;
    private final String referenceName;

    public FixCreateEdgeStatement(String fromPatternNodeName, boolean fromNameIsTemp, String toPatternNodeName, boolean toNameIsTemp, String referenceName) {
        this.fromPatternNodeName = fromPatternNodeName;
        this.fromNameIsTemp = fromNameIsTemp;
        this.toPatternNodeName = toPatternNodeName;
        this.toNameIsTemp = toNameIsTemp;
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

    public boolean isFromNameIsTemp() {
        return fromNameIsTemp;
    }

    public boolean isToNameIsTemp() {
        return toNameIsTemp;
    }
}
