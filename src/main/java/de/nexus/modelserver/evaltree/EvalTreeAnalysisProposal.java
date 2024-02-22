package de.nexus.modelserver.evaltree;

import de.nexus.modelserver.Pattern;

public class EvalTreeAnalysisProposal {
    private final EvalTreeAnalysisProposalType proposalType;
    private final Pattern pattern;
    private final boolean unresolvable;

    public EvalTreeAnalysisProposal(EvalTreeAnalysisProposalType proposalType, Pattern pattern) {
        this.proposalType = proposalType;
        this.pattern = pattern;
        this.unresolvable = false;
    }

    public EvalTreeAnalysisProposal(EvalTreeAnalysisProposalType proposalType, Pattern pattern, boolean unresolvable) {
        this.proposalType = proposalType;
        this.pattern = pattern;
        this.unresolvable = unresolvable;
    }

    public EvalTreeAnalysisProposalType getProposalType() {
        return proposalType;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public boolean isUnresolvable() {
        return unresolvable;
    }
}
