package de.nexus.mmlcli.constraint.entity;

import java.util.ArrayList;

public class PatternEntity {
    private String name;
    private String patternId;
    private final ArrayList<SupportPatternInvocationEntity> pac = new ArrayList<>();
    private final ArrayList<SupportPatternInvocationEntity> nac = new ArrayList<>();
    private final ArrayList<PatternNodeEntity> nodes = new ArrayList<>();
    private final ArrayList<AttributeConstraintEntity> constraints = new ArrayList<>();
    private final ArrayList<EdgeEntity> edges = new ArrayList<>();

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
}
