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

    public String toJavaCode() {
        String fixRegistrations = this.fixContainer.stream().map(x -> String.format(".registerFix(%s)", x.toJavaCode())).collect(Collectors.joining(""));
        return String.format("this.registerPatternDeclaration(new PatternDeclaration(\"%s\", \"%s\")%s);", this.pattern.getName(), this.name, fixRegistrations);
    }
}
