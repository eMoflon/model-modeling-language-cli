package de.nexus.mmlcli.constraint.entity;

import org.eclipse.emf.ecore.EClass;

import java.util.*;

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
        HashMap<EClass, HashSet<PatternNodeEntity>> lookupTable = new HashMap<>();
        HashMap<EClass, HashSet<PatternNodeEntity>> superTypeUsages = new HashMap<>();
        this.nodes.forEach(node -> {
            EClass nodeClass = node.getEClass();
            lookupTable.putIfAbsent(nodeClass, new HashSet<>());
            lookupTable.get(nodeClass).add(node);

            for (EClass superType : nodeClass.getEAllSuperTypes()) {
                superTypeUsages.putIfAbsent(superType, new HashSet<>());
                superTypeUsages.get(superType).add(node);
            }
        });

        ArrayList<NodeConstraintEntity> nodeConstraints = new ArrayList<>();

        for (Map.Entry<EClass, HashSet<PatternNodeEntity>> group : lookupTable.entrySet()) {
            ArrayList<PatternNodeEntity> groupNodes = new ArrayList<>(group.getValue());
            for (int i = 0; i < groupNodes.size(); i++) {
                for (int j = i + 1; j < groupNodes.size(); j++) {
                    nodeConstraints.add(new NodeConstraintEntity(groupNodes.get(i), groupNodes.get(j), NodeConstraintOperator.UNEQUAL));
                }
            }

            List<EClass> superTypeClasses = lookupTable.keySet().stream().filter(superTypeUsages::containsKey).toList();
            for (EClass superTypeClass : superTypeClasses) {
                ArrayList<PatternNodeEntity> superTypeClassNodes = new ArrayList<>(lookupTable.get(superTypeClass));
                ArrayList<PatternNodeEntity> superTypeExtendingNodes = new ArrayList<>(superTypeUsages.get(superTypeClass));
                for (PatternNodeEntity superTypeClassNode : superTypeClassNodes) {
                    for (PatternNodeEntity superTypeExtendingNode : superTypeExtendingNodes) {
                        if (superTypeClassNode == superTypeExtendingNode) {
                            continue;
                        }
                        nodeConstraints.add(new NodeConstraintEntity(superTypeClassNode, superTypeExtendingNode, NodeConstraintOperator.UNEQUAL));
                    }
                }
            }
        }

        return nodeConstraints;
    }
}
