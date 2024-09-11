package de.nexus.modelserver;

import de.nexus.expr.ExpressionEntity;
import de.nexus.modelserver.evaltree.EvalTree;
import de.nexus.modelserver.evaltree.EvalTreeFixProposer;
import de.nexus.modelserver.proto.ModelServerConstraints;

import java.util.*;
import java.util.stream.Collectors;

public abstract class AbstractConstraint {
    private final List<ExpressionEntity> assertions = new ArrayList<>();
    private final Map<String, PatternDeclaration> patternDeclarations = new HashMap<>();
    private boolean violated = false;
    private List<EvalTree> evalTrees;
    private List<Optional<ModelServerConstraints.FixProposalContainer>> computedProposals = null;

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

    public PatternDeclaration getPatternDeclaration(String patternVariableName) {
        return this.patternDeclarations.get(patternVariableName);
    }

    public Pattern getPattern(String patternVarName) {
        return this.patternDeclarations.get(patternVarName).getPattern();
    }

    public Map<String, PatternDeclaration> getPatternDeclarations() {
        return patternDeclarations;
    }

    public void evaluate(PatternRegistry patternRegistry) {
        this.evalTrees = this.assertions.stream().map(expr -> EvalTree.build(expr, patternRegistry, this)).collect(Collectors.toList());
        this.violated = !this.evalTrees.stream().allMatch(EvalTree::getState);
        this.computedProposals = null;
    }

    public void computeProposals(IndexedEMFLoader emfLoader) {
        EvalTreeFixProposer proposer = new EvalTreeFixProposer(this, emfLoader);
        this.computedProposals = new ArrayList<>();
        for (EvalTree evalTree : this.evalTrees) {
            Optional<ModelServerConstraints.FixProposalContainer> proposal = proposer.getProposals(evalTree.getRoot(), true);
            computedProposals.add(proposal);
        }
    }

    public List<Optional<ModelServerConstraints.FixProposalContainer>> getProposals() {
        return this.computedProposals;
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
