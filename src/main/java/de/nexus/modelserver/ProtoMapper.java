package de.nexus.modelserver;

import de.nexus.modelserver.evaltree.EvalTree;
import de.nexus.modelserver.evaltree.EvalTreeAnalysisProposal;
import de.nexus.modelserver.proto.ModelServerConstraints;
import de.nexus.modelserver.proto.ModelServerPatterns;

import java.util.List;
import java.util.stream.Collectors;

public class ProtoMapper {
    public static ModelServerPatterns.Pattern map(Pattern pattern) {
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

    public static ModelServerConstraints.Constraint map(AbstractConstraint constraint) {
        return ModelServerConstraints.Constraint.newBuilder()
                .setTitle(constraint.getTitle())
                .setDescription(constraint.getDescription())
                .setName(constraint.getName())
                .setViolated(constraint.isViolated())
                .addAllAssertions(constraint.getEvalTrees().stream().map(ProtoMapper::map).toList())
                .addAllProposals(constraint.getProposals().stream().filter(x -> !x.isUnresolvable()).map(x -> ProtoMapper.map(x, constraint)).toList())
                .build();
    }

    public static ModelServerConstraints.ConstraintAssertion map(EvalTree evalTree) {
        return ModelServerConstraints.ConstraintAssertion.newBuilder()
                .setExpression(evalTree.getRoot().toFormattedString(0))
                .setViolated(!evalTree.getState())
                .build();
    }

    public static ModelServerConstraints.FixProposal map(EvalTreeAnalysisProposal proposal, AbstractConstraint constraint) {
        ModelServerConstraints.FixProposalType type = switch (proposal.getProposalType()) {
            case ENABLE_PATTERN -> ModelServerConstraints.FixProposalType.ENABLE_PATTERN;
            case DISABLE_PATTERN -> ModelServerConstraints.FixProposalType.DISABLE_PATTERN;
        };

        List<FixContainer> fixContainers = switch (proposal.getProposalType()) {
            case ENABLE_PATTERN ->
                    constraint.getPatternDeclarations().get(proposal.getPatternVariable()).getEnablingFixes();
            case DISABLE_PATTERN ->
                    constraint.getPatternDeclarations().get(proposal.getPatternVariable()).getDisablingFixes();
        };

        List<ModelServerConstraints.FixVariant> variants = fixContainers.stream().map(ProtoMapper::map).toList();

        return ModelServerConstraints.FixProposal.newBuilder()
                .setType(type)
                .setPatternName(proposal.getPattern().getName())
                .addAllVariants(variants)
                .build();
    }

    public static ModelServerConstraints.FixVariant map(FixContainer fixContainer) {
        List<ModelServerConstraints.FixStatement> statements = fixContainer.getStatements().stream().map(ProtoMapper::map).toList();

        return ModelServerConstraints.FixVariant.newBuilder()
                .addAllStatements(statements)
                .build();
    }

    public static ModelServerConstraints.FixStatement map(FixStatement fixStatement) {
        if (fixStatement instanceof FixInfoStatement infoStatement) {
            return ModelServerConstraints.FixStatement.newBuilder()
                    .setType(ModelServerConstraints.FixStatementType.INFO)
                    .setInfoStatement(
                            ModelServerConstraints.FixInfoStatement.newBuilder()
                                    .setMsg(infoStatement.getMsg())
                                    .build())
                    .build();
        }
        throw new UnsupportedOperationException("Could not map unsupported FixStatement to proto: " + fixStatement.getClass().getName());
    }
}
