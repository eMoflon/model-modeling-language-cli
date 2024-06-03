package de.nexus.modelserver.evaltree;

import de.nexus.expr.BinaryOperator;
import de.nexus.expr.PatternPrimaryExpressionEntity;
import de.nexus.expr.UnaryOperator;
import de.nexus.modelserver.*;
import de.nexus.modelserver.proto.ModelServerConstraints;
import de.nexus.modelserver.runtime.EmptyMatch;
import de.nexus.modelserver.runtime.IMatch;

import java.util.*;

public class EvalTreeFixProposer {
    private final AbstractConstraint constraint;
    private final IndexedEMFLoader emfLoader;

    public EvalTreeFixProposer(AbstractConstraint constraint, IndexedEMFLoader emfLoader) {
        this.constraint = constraint;
        this.emfLoader = emfLoader;
    }

    /**
     * Compute proposals for a leaf
     *
     * @param leaf        a EvalTree leaf
     * @param targetValue the boolean target value
     * @return a FixProposalContainer (optionally)
     * @throws RuntimeException for unsupported operations
     */
    public Optional<ModelServerConstraints.FixProposalContainer> getProposals(EvalTreeLeaf leaf, boolean targetValue) {
        if (leaf.getValue().isBoolean()) {
            if (leaf.getValue().getAsBoolean() == targetValue) {
                return Optional.empty();
            }
            ModelServerConstraints.FixProposalType proposalType = leaf.getValue().getAsBoolean() ? ModelServerConstraints.FixProposalType.DISABLE_PATTERN : ModelServerConstraints.FixProposalType.ENABLE_PATTERN;
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

    /**
     * Compute proposals for the children of a binary node
     *
     * @param biNode      a binary EvalTree node
     * @param targetValue the boolean target value
     * @return a FixProposalContainer (optionally)
     * @throws RuntimeException for unsupported operations
     */
    public Optional<ModelServerConstraints.FixProposalContainer> getProposals(EvalTreeBiNode biNode, boolean targetValue) {
        if (biNode.getValue().isBoolean()) {
            if (biNode.getValue().getAsBoolean() == targetValue) {
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

    /**
     * Prune the EvalTree
     * <p>
     * There are multiple cases that make the EvalTree unnecessary verbose:
     * 1. SINGLE_FIX proposals in nodes below the root
     * 2. Nested FIX_ALL and FIX_OR (FIX_ALL inside FIX_ALL could be a single FIX_ALL)
     * 3. FIX_ALL and FIX_OR if there is just a single FixProposalContainer inside
     * <p>
     * These methods prune those cases:
     * 1. Unwrap FixProposals in SINGLE_FIX containers, if they are below root level
     * 2. Merge FIX_ALL and FIX_OR FixProposalContainers if they are directly related
     * 3. Skip FIX_ALL and FIX_OR FixProposalContainers if there is just a single child
     *
     * @param targetContainerType the container type that is desired for the result
     * @param container1          a FixProposalContainer
     * @param container2          another FixProposalContainer
     * @return the prunes FixProposalContainers
     */
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

    /**
     * Compute proposals for the children of a unary node
     *
     * @param uniNode     a unary EvalTree node
     * @param targetValue the boolean target value
     * @return a FixProposalContainer (optionally)
     * @throws RuntimeException for unsupported operations
     */
    public Optional<ModelServerConstraints.FixProposalContainer> getProposals(EvalTreeUniNode uniNode, boolean targetValue) {
        if (uniNode.getValue().isBoolean()) {
            if (uniNode.getValue().getAsBoolean() == targetValue) {
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

    /**
     * Compute proposals for a IEvalTreeNode
     *
     * @param treeNode    a EvalTree node
     * @param targetValue the boolean target value
     * @return a FixProposalContainer (optionally)
     * @throws IllegalArgumentException for unsupported operations
     */
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

    /**
     * Compute a list of all FixMatches for a specific pattern variable
     *
     * @param proposalType    the proposal type (enable/diable)
     * @param patternVariable the name of the requested pattern variable
     * @return a list of all FixMatche for the requested variable
     */
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

        Set<IMatch> matches = new HashSet<>(pattern.getMatches());

        // add the EmptyMatch if there is a fix proposal with the empty modifier
        if (fixContainers.stream().anyMatch(FixContainer::isEmptyMatchFix)) {
            matches.add(EmptyMatch.INSTANCE);
        }

        List<ModelServerConstraints.FixMatch> serializedMatches = new ArrayList<>();
        matches.forEach(match -> serializedMatches.add(ProtoMapper.map(match, fixContainers, emfLoader)));
        return serializedMatches;
    }
}
