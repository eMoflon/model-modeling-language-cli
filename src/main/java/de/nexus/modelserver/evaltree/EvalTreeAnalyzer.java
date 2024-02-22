package de.nexus.modelserver.evaltree;

import de.nexus.expr.BinaryOperator;
import de.nexus.expr.PatternPrimaryExpressionEntity;
import de.nexus.expr.PrimitivePrimaryExpressionEntity;
import de.nexus.expr.UnaryOperator;
import de.nexus.modelserver.AbstractConstraint;
import de.nexus.modelserver.Pattern;
import de.nexus.modelserver.PatternRegistry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class EvalTreeAnalyzer {
    private final EvalTree tree;
    private final PatternRegistry patternRegistry;
    private final AbstractConstraint constraint;

    public EvalTreeAnalyzer(EvalTree tree, PatternRegistry patternRegistry, AbstractConstraint constraint) {
        this.tree = tree;
        this.patternRegistry = patternRegistry;
        this.constraint = constraint;
    }

    public List<EvalTreeAnalysisProposal> analyze() {
        Optional<IEvalTreeNode> firstInvalidNode = findFirstFailedNode(this.tree.getRoot(), true);
        if (firstInvalidNode.isEmpty()) {
            return Collections.emptyList();
        }

        boolean targetState;

        if (firstInvalidNode.get().getValue() instanceof EvalTreeValueBoolean boolValue) {
            targetState = !boolValue.getValue();
        } else {
            throw new IllegalArgumentException("Could not retrieve boolean node value!");
        }

        return getProposals(firstInvalidNode.get(), targetState);
    }

    private List<EvalTreeAnalysisProposal> getProposals(IEvalTreeNode node, boolean targetState) {
        if (node instanceof EvalTreeLeaf leaf) {
            if (leaf.getValue() instanceof EvalTreeValueBoolean boolValue) {
                if (boolValue.getValue() != targetState) {
                    EvalTreeAnalysisProposalType proposalType = boolValue.getValue() ? EvalTreeAnalysisProposalType.DISABLE_PATTERN : EvalTreeAnalysisProposalType.ENABLE_PATTERN;
                    if (((EvalTreeLeaf) node).getExpression() instanceof PatternPrimaryExpressionEntity patternExpr) {
                        String patternName = this.constraint.getPatternDeclarations().get(patternExpr.getPatternName()).getPatternName();
                        Pattern pattern = this.patternRegistry.getPattern(patternName);
                        return List.of(new EvalTreeAnalysisProposal(proposalType, pattern, false));
                    } else {
                        return List.of(new EvalTreeAnalysisProposal(proposalType, null, true));
                    }
                } else {
                    return Collections.emptyList();
                }
            } else {
                throw new UnsupportedOperationException("Proposal analysis currently does not support leafs of type: " + leaf.getValue().getClass().getName());
            }
        } else if (node instanceof EvalTreeUniNode uniNode) {
            if (uniNode.getExpression().getOperator().equals(UnaryOperator.NEGATION)) {
                return getProposals(uniNode.getChild(), !targetState);
            } else {
                throw new UnsupportedOperationException("Proposal analysis currently does not support unary operator: " + uniNode.getExpression().getOperator().name());
            }
        } else if (node instanceof EvalTreeBiNode biNode) {
            if (biNode.getExpr().getOperator().equals(BinaryOperator.LOGICAL_AND) || biNode.getExpr().getOperator().equals(BinaryOperator.LOGICAL_OR)) {
                ArrayList<EvalTreeAnalysisProposal> proposals = new ArrayList<>();
                if (biNode.getLeftChild().getValue() instanceof EvalTreeValueBoolean leftBool) {
                    if (targetState && !leftBool.getValue()) {
                        proposals.addAll(getProposals(biNode.getLeftChild(), targetState));
                    } else if (!targetState && leftBool.getValue()) {
                        proposals.addAll(getProposals(biNode.getLeftChild(), targetState));
                    }
                } else {
                    throw new IllegalArgumentException("Unexpected logical and operand on the left side: " + biNode.getLeftChild().getValue().getClass().getName());
                }
                if (biNode.getRightChild().getValue() instanceof EvalTreeValueBoolean rightBool) {
                    if (targetState && !rightBool.getValue()) {
                        proposals.addAll(getProposals(biNode.getRightChild(), targetState));
                    } else if (!targetState && rightBool.getValue()) {
                        proposals.addAll(getProposals(biNode.getRightChild(), targetState));
                    }
                } else {
                    throw new IllegalArgumentException("Unexpected logical and operand on the right side: " + biNode.getLeftChild().getValue().getClass().getName());
                }
                return proposals;
            } else {
                throw new UnsupportedOperationException("Proposal analysis currently does not support binary operator: " + biNode.getExpr().getOperator().name());
            }
        }
        throw new UnsupportedOperationException("Unsupported EvalTreeNodeType: " + node.getClass().getName());
    }

    private Optional<IEvalTreeNode> findFirstFailedNode(IEvalTreeNode node, boolean reference) {
        if (node.getValue() instanceof EvalTreeValueBoolean boolVal) {
            if (boolVal.getValue() != reference) {
                return Optional.of(node);
            }
        }
        if (node instanceof EvalTreeBiNode biNode) {
            if (biNode.getExpr().getOperator().isBooleanOperator()) {
                Optional<IEvalTreeNode> leftEval = findFirstFailedNode(biNode.getLeftChild(), reference);
                if (leftEval.isPresent()) {
                    return leftEval;
                }
                return findFirstFailedNode(biNode.getRightChild(), reference);
            }
            return Optional.of(node);
        } else if (node instanceof EvalTreeUniNode uniNode) {
            if (uniNode.getExpression().getOperator().equals(UnaryOperator.NEGATION)) {
                return findFirstFailedNode(uniNode.getChild(), !reference);
            }
        } else if (node instanceof EvalTreeLeaf leaf) {
            if (leaf.getExpression() instanceof PatternPrimaryExpressionEntity patternExpr) {
                String patternName = this.constraint.getPatternDeclarations().get(patternExpr.getPatternName()).getPatternName();
                Pattern pattern = this.patternRegistry.getPattern(patternName);
                if (pattern.hasAny() == reference) {
                    return Optional.of(node);
                }
                return Optional.empty();
            } else if (leaf.getExpression() instanceof PrimitivePrimaryExpressionEntity<?>) {
                return Optional.of(node);
            }
        }
        throw new IllegalArgumentException("Unsupported IEvalTreeNode subtype!");
    }
}
