package de.nexus.mmlcli.constraint.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class PatternEntity {
    private String name;
    private String patternId;
    private boolean disableDefaultNodeConstraints;
    private final ArrayList<SupportPatternInvocationEntity> pac = new ArrayList<>();
    private final ArrayList<SupportPatternInvocationEntity> nac = new ArrayList<>();
    private final ArrayList<PatternNodeEntity> nodes = new ArrayList<>();
    private final ArrayList<AttributeConstraintEntity> constraints = new ArrayList<>();
    private final ArrayList<EdgeEntity> edges = new ArrayList<>();
    private final ArrayList<NodeConstraintEntity> nodeConstraints = new ArrayList<>();

    public String getName() {
        return name;
    }

    public String getPatternId() {
        return patternId;
    }

    public ArrayList<SupportPatternInvocationEntity> getPac() {
        return pac;
    }

    public ArrayList<SupportPatternInvocationEntity> getNac() {
        return nac;
    }

    public ArrayList<PatternNodeEntity> getNodes() {
        return nodes;
    }

    public ArrayList<EdgeEntity> getEdges() {
        return edges;
    }

    public ArrayList<AttributeConstraintEntity> getConstraints() {
        return constraints;
    }

    public ArrayList<NodeConstraintEntity> getNodeConstraints() {
        if (this.disableDefaultNodeConstraints) {
            return nodeConstraints;
        } else {
            return getDefaultNodeConstraints();
        }
    }

    private ArrayList<NodeConstraintEntity> getDefaultNodeConstraints() {
        HashMap<String, HashSet<PatternNodeEntity>> lookupTable = new HashMap<>();
        this.nodes.forEach(node -> {
            lookupTable.putIfAbsent(node.getFQName(), new HashSet<>());
            lookupTable.get(node.getFQName()).add(node);
        });

        ArrayList<NodeConstraintEntity> nodeConstraints = new ArrayList<>();

        for (HashSet<PatternNodeEntity> group : lookupTable.values()) {
            ArrayList<PatternNodeEntity> groupNodes = new ArrayList<>(group);
            for (int i = 0; i < groupNodes.size(); i++) {
                for (int j = i + 1; j < groupNodes.size(); j++) {
                    nodeConstraints.add(new NodeConstraintEntity(groupNodes.get(i), groupNodes.get(j), NodeConstraintOperator.UNEQUAL));
                }
            }
        }

        return nodeConstraints;
    }
}
