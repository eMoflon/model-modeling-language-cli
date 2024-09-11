package de.nexus.mmlcli.constraint.entity;

public class NodeBindingEntity {
    private String node1;
    private transient PatternNodeEntity patternNode1;
    private String node2;
    private transient PatternNodeEntity patternNode2;

    public String getNode1() {
        return node1;
    }

    public String getNode2() {
        return node2;
    }

    public PatternNodeEntity getPatternNode1() {
        return patternNode1;
    }

    public PatternNodeEntity getPatternNode2() {
        return patternNode2;
    }

    public void setPatternNode1(PatternNodeEntity patternNode1) {
        this.patternNode1 = patternNode1;
    }

    public void setPatternNode2(PatternNodeEntity patternNode2) {
        this.patternNode2 = patternNode2;
    }
}
