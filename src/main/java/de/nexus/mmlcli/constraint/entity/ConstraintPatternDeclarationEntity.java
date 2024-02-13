package de.nexus.mmlcli.constraint.entity;

public class ConstraintPatternDeclarationEntity {
    private String declarationId;
    private String patternId;
    private volatile PatternEntity pattern;
    private String name;

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

    public void setPattern(PatternEntity pattern) {
        this.pattern = pattern;
    }
}
