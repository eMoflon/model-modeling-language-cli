package de.nexus.mmlcli.constraint.entity.expr;

public class PatternPrimaryExpressionEntity implements PrimaryExpressionEntity {
    private final String nodeId;
    private final String patternName;

    public PatternPrimaryExpressionEntity(String nodeId, String patternName) {
        this.nodeId = nodeId;
        this.patternName = patternName;
    }

    @Override
    public PrimaryExpressionEntityType getType() {
        return PrimaryExpressionEntityType.PATTERN_INVOCATION;
    }

    public String getNodeId() {
        return nodeId;
    }

    public String getPatternName() {
        return patternName;
    }

    @Override
    public String toJavaCode() {
        return String.format("new PatternPrimaryExpressionEntity(\"%s\", \"%s\")", this.nodeId, this.patternName);
    }
}