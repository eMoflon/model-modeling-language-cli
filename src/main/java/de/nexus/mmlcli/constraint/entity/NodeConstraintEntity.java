package de.nexus.mmlcli.constraint.entity;

public class NodeConstraintEntity {
    private String node1Id;
    private volatile PatternNodeEntity node1;
    private String node2Id;
    private volatile PatternNodeEntity node2;
    private final String operator;
    private volatile NodeConstraintOperator nodeOperator;

    public NodeConstraintEntity(PatternNodeEntity node1, PatternNodeEntity node2, String operator) {
        this.node1 = node1;
        this.node2 = node2;
        this.operator = operator;
        this.nodeOperator = NodeConstraintOperator.fromStringOperator(operator);
    }

    public NodeConstraintEntity(PatternNodeEntity node1, PatternNodeEntity node2, NodeConstraintOperator operator) {
        this.node1 = node1;
        this.node2 = node2;
        this.operator = operator.toStringOperator();
        this.nodeOperator = operator;
    }

    public PatternNodeEntity getNode1() {
        return node1;
    }

    public PatternNodeEntity getNode2() {
        return node2;
    }

    public String getOperator() {
        return operator;
    }

    public String getNode1Id() {
        return node1Id;
    }

    public void setNode1(PatternNodeEntity node1) {
        this.node1 = node1;
    }

    public void setNode2(PatternNodeEntity node2) {
        this.node2 = node2;
    }

    public String getNode2Id() {
        return node2Id;
    }

    public NodeConstraintOperator getNodeOperator() {
        return nodeOperator;
    }

    public void setNodeOperator(NodeConstraintOperator nodeOperator) {
        this.nodeOperator = nodeOperator;
    }
}
