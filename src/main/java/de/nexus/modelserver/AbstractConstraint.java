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
    private boolean violated = false;
    private List<EvalTree> evalTrees;

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

    public Map<String, PatternDeclaration> getPatternDeclarations() {
        return patternDeclarations;
    }

    public void evaluate(PatternRegistry patternRegistry) {
        this.evalTrees = this.assertions.stream().map(expr -> EvalTree.build(expr, patternRegistry, this)).collect(Collectors.toList());
        this.violated = !this.evalTrees.stream().allMatch(EvalTree::getState);
    }

    public boolean isViolated() {
        return violated;
    }

    public List<EvalTree> getEvalTrees() {
        return evalTrees;
    }

    public List<FixContainer> getEnablingFixesForPatternVariable(String patternVar) {
        return this.patternDeclarations.get(patternVar).getEnablingFixes();
    }

    public List<FixContainer> getDisablingFixesForPatternVariable(String patternVar) {
        return this.patternDeclarations.get(patternVar).getDisablingFixes();
    }
}
