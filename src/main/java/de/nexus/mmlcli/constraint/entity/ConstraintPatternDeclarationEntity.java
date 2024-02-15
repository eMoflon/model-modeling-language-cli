package de.nexus.mmlcli.constraint.entity;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class ConstraintPatternDeclarationEntity {
    private String declarationId;
    private String patternId;
    private volatile PatternEntity pattern;
    private String name;
    private ArrayList<FixContainerEntity> fixContainer;

    public String getDeclarationId() {
        return declarationId;
    }

    public String getPatternId() {
        return patternId;
    }

    public PatternEntity getPattern() {
        return pattern;
    }

    public String getName() {
        return name;
    }

    public ArrayList<FixContainerEntity> getFixContainer() {
        return fixContainer;
    }

    public void setPattern(PatternEntity pattern) {
        this.pattern = pattern;
    }
}
