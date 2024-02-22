package de.nexus.modelserver;

import de.nexus.modelserver.evaltree.EvalTree;
import de.nexus.modelserver.evaltree.EvalTreeAnalysisProposal;
import de.nexus.modelserver.proto.ModelServerConstraints;
import de.nexus.modelserver.proto.ModelServerPatterns;

import java.util.stream.Collectors;

public class ProtoMapper {
    public static ModelServerPatterns.Pattern mapPattern(Pattern pattern) {
        return ModelServerPatterns.Pattern.newBuilder()
                .setName(pattern.getName())
                .setNumberOfMatches(pattern.getMatches().size())
                .addAllMatches(
                        pattern.getMatches().stream().map(x ->
                                ModelServerPatterns.Match.newBuilder()
                                        .addAllNodes(x.getObjects().stream().map(Object::toString).collect(Collectors.toList()))
                                        .build()
                        ).collect(Collectors.toList())
                ).build();
    }

    public static ModelServerConstraints.Constraint mapConstraint(AbstractConstraint constraint) {
        return ModelServerConstraints.Constraint.newBuilder()
                .setTitle(constraint.getTitle())
                .setDescription(constraint.getDescription())
                .setName(constraint.getName())
                .setViolated(constraint.isViolated())
                .addAllAssertions(constraint.getEvalTrees().stream().map(ProtoMapper::mapEvalTree).toList())
                .addAllProposals(constraint.getProposals().stream().filter(x -> !x.isUnresolvable()).map(ProtoMapper::mapProposals).toList())
                .build();
    }

    public static ModelServerConstraints.ConstraintAssertion mapEvalTree(EvalTree evalTree) {
        return ModelServerConstraints.ConstraintAssertion.newBuilder()
                .setExpression(evalTree.getRoot().toFormattedString(0))
                .setViolated(!evalTree.getState())
                .build();
    }

    public static ModelServerConstraints.FixProposal mapProposals(EvalTreeAnalysisProposal proposal) {
        ModelServerConstraints.FixProposalType type = switch (proposal.getProposalType()) {
            case ENABLE_PATTERN -> ModelServerConstraints.FixProposalType.ENABLE_PATTERN;
            case DISABLE_PATTERN -> ModelServerConstraints.FixProposalType.DISABLE_PATTERN;
        };

        return ModelServerConstraints.FixProposal.newBuilder()
                .setType(type)
                .setPatternName(proposal.getPattern().getName())
                .build();
    }
}
