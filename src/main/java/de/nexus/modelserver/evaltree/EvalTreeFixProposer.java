package de.nexus.modelserver.evaltree;

import de.nexus.expr.BinaryOperator;
import de.nexus.expr.PatternPrimaryExpressionEntity;
import de.nexus.expr.UnaryOperator;
import de.nexus.modelserver.*;
import de.nexus.modelserver.proto.ModelServerConstraints;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class EvalTreeFixProposer {
    private final AbstractConstraint constraint;
    private final IndexedEMFLoader emfLoader;

    public EvalTreeFixProposer(AbstractConstraint constraint, IndexedEMFLoader emfLoader) {
        this.constraint = constraint;
        this.emfLoader = emfLoader;
    }

    public Optional<ModelServerConstraints.FixProposalContainer> getProposals(EvalTreeLeaf leaf, boolean targetValue) {
        if (leaf.getValue() instanceof EvalTreeValueBoolean booleanValue) {
            if (booleanValue.getValue() == targetValue) {
                return Optional.empty();
            }
            ModelServerConstraints.FixProposalType proposalType = booleanValue.getValue() ? ModelServerConstraints.FixProposalType.DISABLE_PATTERN : ModelServerConstraints.FixProposalType.ENABLE_PATTERN;
            if (leaf.getExpression() instanceof PatternPrimaryExpressionEntity patternExpr) {
                List<ModelServerConstraints.FixMatch> matches = getFixMatches(proposalType, patternExpr.getPatternName());
                ModelServerConstraints.FixProposal proposal = ModelServerConstraints.FixProposal.newBuilder().setType(proposalType).setPatternName(patternExpr.getPatternName()).addAllMatches(matches).build();
                ModelServerConstraints.FixProposalContainer proposalContainer = ModelServerConstraints.FixProposalContainer.newBuilder().setType(ModelServerConstraints.FixProposalContainerType.SINGLE_FIX).addProposals(proposal).build();
                return Optional.of(proposalContainer);
            } else {
                return Optional.of(ModelServerConstraints.FixProposalContainer.newBuilder().setType(ModelServerConstraints.FixProposalContainerType.UNRESOLVABLE_CASE).build());
            }
        } else {
            throw new RuntimeException("EvalTreeFixProposer does currently not support non-boolean EvalTreeValues!");
        }
    }

    public Optional<ModelServerConstraints.FixProposalContainer> getProposals(EvalTreeBiNode biNode, boolean targetValue) {
        if (biNode.getValue() instanceof EvalTreeValueBoolean booleanValue) {
            if (booleanValue.getValue() == targetValue) {
                return Optional.empty();
            }
            Optional<ModelServerConstraints.FixProposalContainer> leftContainer = getProposals(biNode.getLeftChild(), targetValue);
            Optional<ModelServerConstraints.FixProposalContainer> rightContainer = getProposals(biNode.getRightChild(), targetValue);

            if (biNode.getExpression().getOperator().equals(BinaryOperator.LOGICAL_AND)) {
                if (leftContainer.isEmpty() && rightContainer.isPresent()) {
                    return rightContainer;
                } else if (leftContainer.isPresent() && rightContainer.isEmpty()) {
                    return leftContainer;
                } else if (leftContainer.isPresent() && rightContainer.isPresent()) {
                    return Optional.of(mergeProposalContainers(ModelServerConstraints.FixProposalContainerType.FIX_ALL, leftContainer.get(), rightContainer.get()));
                } else {
                    throw new RuntimeException("Trying to generate ProposalContainer although both children are empty!");
                }
            } else if (biNode.getExpression().getOperator().equals(BinaryOperator.LOGICAL_OR)) {
                if (leftContainer.isPresent() && rightContainer.isPresent()) {
                    return Optional.of(mergeProposalContainers(ModelServerConstraints.FixProposalContainerType.FIX_ONE, leftContainer.get(), rightContainer.get()));
                } else {
                    throw new RuntimeException("Trying to generate ProposalContainer although both children are empty!");
                }
            } else {
                throw new RuntimeException(String.format("EvalTreeFixProposer does currently not support EvalTreeUniNodes with operator: %s!", biNode.getExpression().getOperator().name()));
            }
        } else {
            throw new RuntimeException("EvalTreeFixProposer does currently not support non-boolean EvalTreeValues!");
        }
    }

    private ModelServerConstraints.FixProposalContainer mergeProposalContainers(ModelServerConstraints.FixProposalContainerType targetContainerType, ModelServerConstraints.FixProposalContainer container1, ModelServerConstraints.FixProposalContainer container2) {
        ModelServerConstraints.FixProposalContainerType container1Type = container1.getType();
        ModelServerConstraints.FixProposalContainerType container2Type = container2.getType();
        boolean container1IsSingleProposal = container1Type == ModelServerConstraints.FixProposalContainerType.SINGLE_FIX;
        boolean container2IsSingleProposal = container2Type == ModelServerConstraints.FixProposalContainerType.SINGLE_FIX;

        if (targetContainerType == ModelServerConstraints.FixProposalContainerType.UNKNOWN_CONTAINER_TYPE || targetContainerType == ModelServerConstraints.FixProposalContainerType.SINGLE_FIX) {
            throw new RuntimeException("Cannot merge ProposalContainers to targetType: " + targetContainerType.name());
        }

        if (targetContainerType == container1Type && targetContainerType == container2Type) {
            return ModelServerConstraints.FixProposalContainer.newBuilder()
                    .setType(targetContainerType)
                    .addAllProposals(container1.getProposalsList())
                    .addAllProposals(container2.getProposalsList())
                    .addAllProposalContainers(container1.getProposalContainersList())
                    .addAllProposalContainers(container2.getProposalContainersList())
                    .build();
        } else if (targetContainerType != container1Type && targetContainerType != container2Type) {
            ModelServerConstraints.FixProposalContainer.Builder builder = ModelServerConstraints.FixProposalContainer.newBuilder()
                    .setType(targetContainerType);

            if (container1IsSingleProposal) {
                builder.addAllProposals(container1.getProposalsList());
            } else {
                builder.addProposalContainers(container1);
            }

            if (container2IsSingleProposal) {
                builder.addAllProposals(container2.getProposalsList());
            } else {
                builder.addProposalContainers(container2);
            }

            return builder.build();
        } else if (targetContainerType == container1Type && targetContainerType != container2Type) {
            ModelServerConstraints.FixProposalContainer.Builder builder = ModelServerConstraints.FixProposalContainer.newBuilder()
                    .setType(targetContainerType)
                    .addAllProposals(container1.getProposalsList())
                    .addAllProposalContainers(container1.getProposalContainersList());

            if (container2IsSingleProposal) {
                builder.addAllProposals(container2.getProposalsList());
            } else {
                builder.addProposalContainers(container2);
            }

            return builder.build();
        } else if (targetContainerType != container1Type && targetContainerType == container2Type) {
            ModelServerConstraints.FixProposalContainer.Builder builder = ModelServerConstraints.FixProposalContainer.newBuilder()
                    .setType(targetContainerType)
                    .addAllProposals(container2.getProposalsList())
                    .addAllProposalContainers(container2.getProposalContainersList());

            if (container1IsSingleProposal) {
                builder.addAllProposals(container1.getProposalsList());
            } else {
                builder.addProposalContainers(container1);
            }

            return builder.build();
        }
        throw new RuntimeException("Failed to merge two Proposalcontainers!");
    }

    public Optional<ModelServerConstraints.FixProposalContainer> getProposals(EvalTreeUniNode uniNode, boolean targetValue) {
        if (uniNode.getValue() instanceof EvalTreeValueBoolean booleanValue) {
            if (booleanValue.getValue() == targetValue) {
                return Optional.empty();
            }
            if (uniNode.getExpression().getOperator().equals(UnaryOperator.NEGATION)) {
                return getProposals(uniNode.getChild(), !targetValue);
            } else {
                throw new RuntimeException(String.format("EvalTreeFixProposer does currently not support EvalTreeUniNodes with operator: %s!", uniNode.getExpression().getOperator().name()));
            }
        } else {
            throw new RuntimeException("EvalTreeFixProposer does currently not support non-boolean EvalTreeValues!");
        }

    }

    public Optional<ModelServerConstraints.FixProposalContainer> getProposals(IEvalTreeNode treeNode, boolean targetValue) {
        if (treeNode instanceof EvalTreeLeaf leaf) {
            return getProposals(leaf, targetValue);
        } else if (treeNode instanceof EvalTreeUniNode uniNode) {
            return getProposals(uniNode, targetValue);
        } else if (treeNode instanceof EvalTreeBiNode biNode) {
            return getProposals(biNode, targetValue);
        }
        throw new IllegalArgumentException("Unsupported IEvalTreeNode type!");
    }

    public List<ModelServerConstraints.FixMatch> getFixMatches(ModelServerConstraints.FixProposalType proposalType, String patternVariable) {
        List<FixContainer> fixContainers = switch (proposalType) {
            case ENABLE_PATTERN -> constraint.getEnablingFixesForPatternVariable(patternVariable);
            case DISABLE_PATTERN -> constraint.getDisablingFixesForPatternVariable(patternVariable);
            default -> Collections.emptyList();
        };

        if (fixContainers.isEmpty()) {
            return Collections.emptyList();
        }

        Pattern pattern = constraint.getPattern(patternVariable);

        return pattern.getMatches().stream().map(x -> ProtoMapper.map(x, fixContainers, emfLoader)).toList();
    }
}
