package de.nexus.mmlcli.constraint.entity;

public class FixCreateEdgeEntity implements IFixStatementEntity {
    private final String referenceName;
    private final String fromPatternNodeName;
    private final boolean fromNameIsTemp;
    private final String toPatternNodeName;
    private final boolean toNameIsTemp;

    public FixCreateEdgeEntity(String fromPatternNodeName, String toPatternNodeName, String referenceName, boolean fromNameIsTemp, boolean toNameIsTemp) {
        this.referenceName = referenceName;
        this.fromPatternNodeName = fromPatternNodeName;
        this.toPatternNodeName = toPatternNodeName;
        this.fromNameIsTemp = fromNameIsTemp;
        this.toNameIsTemp = toNameIsTemp;
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

    public boolean isFromNameIsTemp() {
        return fromNameIsTemp;
    }

    public boolean isToNameIsTemp() {
        return toNameIsTemp;
    }

    @Override
    public String toJavaCode() {
        return String.format("new FixCreateEdgeStatement(\"%s\", %b,\"%s\", %b,\"%s\")", this.fromPatternNodeName, this.fromNameIsTemp, this.toPatternNodeName, this.toNameIsTemp, this.referenceName);
    }
}
