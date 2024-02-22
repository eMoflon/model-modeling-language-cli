package de.nexus.modelserver.evaltree;

import de.nexus.modelserver.Pattern;

public class EvalTreeAnalysisProposal {
    private final EvalTreeAnalysisProposalType proposalType;
    private final Pattern pattern;
    private final String patternVariable;
    private final boolean unresolvable;

    public EvalTreeAnalysisProposal(EvalTreeAnalysisProposalType proposalType, Pattern pattern, String patternVariable) {
        this.proposalType = proposalType;
        this.pattern = pattern;
        this.patternVariable = patternVariable;
        this.unresolvable = false;
    }

    public EvalTreeAnalysisProposal(EvalTreeAnalysisProposalType proposalType, Pattern pattern, String patternVariable, boolean unresolvable) {
        this.proposalType = proposalType;
        this.pattern = pattern;
        this.patternVariable = patternVariable;
        this.unresolvable = unresolvable;
    }

    public EvalTreeAnalysisProposalType getProposalType() {
        return proposalType;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public String getPatternVariable() {
        return patternVariable;
    }

    public boolean isUnresolvable() {
        return unresolvable;
    }
}
