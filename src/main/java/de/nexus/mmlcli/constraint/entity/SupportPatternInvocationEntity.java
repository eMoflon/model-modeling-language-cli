package de.nexus.mmlcli.constraint.entity;

import java.util.ArrayList;

public class SupportPatternInvocationEntity {
    private String patternId;
    private transient PatternEntity pattern;
    private final ArrayList<NodeBindingEntity> bindings = new ArrayList<>();

    public String getPatternId() {
        return patternId;
    }

    public PatternEntity getPattern() {
        return pattern;
    }

    public ArrayList<NodeBindingEntity> getBindings() {
        return bindings;
    }

    public void setPattern(PatternEntity pattern) {
        this.pattern = pattern;
    }
}
