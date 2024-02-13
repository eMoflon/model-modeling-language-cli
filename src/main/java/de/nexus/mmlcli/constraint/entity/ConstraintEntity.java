package de.nexus.mmlcli.constraint.entity;

import java.util.ArrayList;

public class ConstraintEntity {
    private String title;
    private String description;
    private String name;
    private final ArrayList<ConstraintPatternDeclarationEntity> patternDeclarations = new ArrayList<>();
    private final ArrayList<ConstraintAssertionEntity> assertions = new ArrayList<>();

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public ArrayList<ConstraintPatternDeclarationEntity> getPatternDeclarations() {
        return patternDeclarations;
    }

    public ArrayList<ConstraintAssertionEntity> getAssertions() {
        return assertions;
    }
}
