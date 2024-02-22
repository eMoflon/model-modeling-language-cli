package de.nexus.modelserver;

import java.util.ArrayList;
import java.util.List;

public class PatternDeclaration {
    private final String patternName;
    private final String patternVariableName;
    private volatile Pattern pattern;
    private final List<FixContainer> enablingFixes = new ArrayList<>();
    private final List<FixContainer> disablingFixes = new ArrayList<>();

    public PatternDeclaration(String patternName, String patternVariableName) {
        this.patternName = patternName;
        this.patternVariableName = patternVariableName;
    }

    public PatternDeclaration registerFix(FixContainer fixContainer) {
        if (fixContainer instanceof EnablingFixContainer) {
            this.enablingFixes.add(fixContainer);
            return this;
        } else if (fixContainer instanceof DisablingFixContainer) {
            this.disablingFixes.add(fixContainer);
            return this;
        }
        throw new IllegalArgumentException("Unknown FixContainer subtype: " + fixContainer.getClass().getName());
    }

    public Pattern getPattern() {
        return pattern;
    }

    public void connectPattern(PatternRegistry registry) {
        Pattern pattern1 = registry.getPattern(this.patternName);
        if (pattern1 != null) {
            this.pattern = pattern1;
        } else {
            throw new IllegalArgumentException("Could not find pattern with name: " + this.patternName);
        }
    }

    public String getPatternName() {
        return patternName;
    }

    public String getPatternVariableName() {
        return patternVariableName;
    }

    public List<FixContainer> getEnablingFixes() {
        return enablingFixes;
    }

    public List<FixContainer> getDisablingFixes() {
        return disablingFixes;
    }
}
