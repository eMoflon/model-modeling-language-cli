package de.nexus.modelserver;

import de.nexus.expr.ExpressionEntity;
import de.nexus.modelserver.evaltree.EvalTree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class AbstractConstraint {
    private final List<ExpressionEntity> assertions = new ArrayList<>();
    private final Map<String, PatternDeclaration> patternDeclarations = new HashMap<>();

    public abstract String getName();

    public abstract String getTitle();

    public abstract String getDescription();

    protected void registerAssertion(ExpressionEntity expr) {
        this.assertions.add(expr);
    }

    public List<ExpressionEntity> getAssertions() {
        return assertions;
    }

    protected void registerPatternDeclaration(PatternDeclaration pDec) {
        this.patternDeclarations.put(pDec.getPatternVariableName(), pDec);
    }

    public List<EvalTree> evaluate(PatternRegistry patternRegistry) {
        return this.assertions.stream().map(expr -> EvalTree.build(expr, patternRegistry)).collect(Collectors.toList());
    }

    public List<FixContainer> getEnablingFixesForPatternVariable(String patternVar) {
        return this.patternDeclarations.get(patternVar).getEnablingFixes();
    }

    public List<FixContainer> getDisablingFixesForPatternVariable(String patternVar) {
        return this.patternDeclarations.get(patternVar).getDisablingFixes();
    }
}
