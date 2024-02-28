package de.nexus.modelserver;

import de.nexus.modelserver.evaltree.EvalTree;
import de.nexus.modelserver.evaltree.EvalTreeAnalysisProposal;
import de.nexus.modelserver.proto.ModelServerConstraints;
import de.nexus.modelserver.proto.ModelServerEditStatements;
import de.nexus.modelserver.proto.ModelServerPatterns;
import de.nexus.modelserver.runtime.IMatch;
import org.emoflon.smartemf.runtime.SmartObject;

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

    public static ModelServerConstraints.Constraint map(AbstractConstraint constraint, IndexedEMFLoader emfLoader) {
        return ModelServerConstraints.Constraint.newBuilder()
                .setTitle(constraint.getTitle())
                .setDescription(constraint.getDescription())
                .setName(constraint.getName())
                .setViolated(constraint.isViolated())
                .addAllAssertions(constraint.getEvalTrees().stream().map(ProtoMapper::map).toList())
                .addAllProposals(constraint.getProposals().stream().filter(x -> !x.isUnresolvable()).map(x -> ProtoMapper.map(x, constraint, emfLoader)).toList())
                .build();
    }

    public static ModelServerConstraints.ConstraintAssertion map(EvalTree evalTree) {
        return ModelServerConstraints.ConstraintAssertion.newBuilder()
                .setExpression(evalTree.getRoot().toFormattedString(0))
                .setViolated(!evalTree.getState())
                .build();
    }

    public static ModelServerConstraints.FixProposal map(EvalTreeAnalysisProposal proposal, AbstractConstraint constraint, IndexedEMFLoader emfLoader) {
        ModelServerConstraints.FixProposalType type = switch (proposal.getProposalType()) {
            case ENABLE_PATTERN -> ModelServerConstraints.FixProposalType.ENABLE_PATTERN;
            case DISABLE_PATTERN -> ModelServerConstraints.FixProposalType.DISABLE_PATTERN;
        };

        List<FixContainer> fixContainers = switch (proposal.getProposalType()) {
            case ENABLE_PATTERN -> constraint.getEnablingFixesForPatternVariable(proposal.getPatternVariable());
            case DISABLE_PATTERN -> constraint.getDisablingFixesForPatternVariable(proposal.getPatternVariable());
        };

        List<ModelServerConstraints.FixMatch> matches = proposal.getPattern().getMatches().stream().map(x -> ProtoMapper.map(x, fixContainers, emfLoader)).toList();

        return ModelServerConstraints.FixProposal.newBuilder()
                .setType(type)
                .setPatternName(proposal.getPattern().getName())
                .addAllMatches(matches)
                .build();
    }

    public static ModelServerConstraints.FixMatch map(IMatch match, List<FixContainer> variants, IndexedEMFLoader emfLoader) {
        List<ModelServerConstraints.FixVariant> protoVariants = variants.stream().map(x -> ProtoMapper.map(match, x, emfLoader)).toList();

        return ModelServerConstraints.FixMatch.newBuilder()
                .addAllVariants(protoVariants)
                .build();
    }

    public static ModelServerConstraints.FixVariant map(IMatch match, FixContainer fixContainer, IndexedEMFLoader emfLoader) {
        List<ModelServerConstraints.FixStatement> statements = fixContainer.getStatements().stream().map(x -> ProtoMapper.map(match, x, emfLoader)).toList();

        return ModelServerConstraints.FixVariant.newBuilder()
                .addAllStatements(statements)
                .build();
    }

    public static ModelServerConstraints.FixStatement map(IMatch match, FixStatement fixStatement, IndexedEMFLoader emfLoader) {
        if (fixStatement instanceof FixInfoStatement infoStatement) {
            return ModelServerConstraints.FixStatement.newBuilder()
                    .setInfoStatement(
                            ModelServerConstraints.FixInfoStatement.newBuilder()
                                    .setMsg(infoStatement.getMsg())
                                    .build()
                    )
                    .build();
        } else if (fixStatement instanceof FixDeleteEdgeStatement deleteEdgeStatement) {
            return ModelServerConstraints.FixStatement.newBuilder()
                    .setEdit(ModelServerEditStatements.EditRequest.newBuilder()
                            .setDeleteEdgeRequest(ProtoMapper.map(match, deleteEdgeStatement, emfLoader))
                            .build()
                    )
                    .build();
        } else if (fixStatement instanceof FixDeleteNodeStatement deleteNodeStatement) {
            return ModelServerConstraints.FixStatement.newBuilder()
                    .setEdit(ModelServerEditStatements.EditRequest.newBuilder()
                            .setDeleteNodeRequest(ProtoMapper.map(match, deleteNodeStatement, emfLoader))
                            .build()
                    )
                    .build();
        } else if (fixStatement instanceof FixCreateEdgeStatement createEdgeStatement) {
            return ModelServerConstraints.FixStatement.newBuilder()
                    .setEdit(ModelServerEditStatements.EditRequest.newBuilder()
                            .setCreateEdgeRequest(ProtoMapper.map(match, createEdgeStatement, emfLoader))
                            .build()
                    )
                    .build();
        } else if (fixStatement instanceof FixCreateNodeStatement createNodeStatement) {
            return ModelServerConstraints.FixStatement.newBuilder()
                    .setEdit(ModelServerEditStatements.EditRequest.newBuilder()
                            .setCreateNodeRequest(ProtoMapper.map(createNodeStatement))
                            .build()
                    )
                    .build();
        } else if (fixStatement instanceof FixSetStatement setStatement) {
            return ModelServerConstraints.FixStatement.newBuilder()
                    .setEdit(ModelServerEditStatements.EditRequest.newBuilder()
                            .setSetAttributeRequest(ProtoMapper.map(match, setStatement, emfLoader))
                            .build()
                    )
                    .build();
        }
        throw new UnsupportedOperationException("Could not map unsupported FixStatement to proto: " + fixStatement.getClass().getName());
    }

    public static ModelServerEditStatements.EditDeleteEdgeRequest map(IMatch match, FixDeleteEdgeStatement fixStatement, IndexedEMFLoader emfLoader) {
        SmartObject fromNode = (SmartObject) match.get(fixStatement.getFromPatternNodeName());
        SmartObject toNode = (SmartObject) match.get(fixStatement.getToPatternNodeName());
        int fromNodeId = emfLoader.getNodeId(fromNode);
        int toNodeId = emfLoader.getNodeId(toNode);

        return ModelServerEditStatements.EditDeleteEdgeRequest.newBuilder()
                .setStartNode(ModelServerEditStatements.Node.newBuilder().setNodeId(fromNodeId).build())
                .setTargetNode(ModelServerEditStatements.Node.newBuilder().setNodeId(toNodeId).build())
                .setReferenceName(fixStatement.getReferenceName())
                .build();
    }

    public static ModelServerEditStatements.EditDeleteNodeRequest map(IMatch match, FixDeleteNodeStatement fixStatement, IndexedEMFLoader emfLoader) {
        SmartObject node = (SmartObject) match.get(fixStatement.getNodeName());
        int nodeId = emfLoader.getNodeId(node);

        return ModelServerEditStatements.EditDeleteNodeRequest.newBuilder()
                .setNode(ModelServerEditStatements.Node.newBuilder()
                        .setNodeId(nodeId)
                        .build()
                )
                .build();
    }

    public static ModelServerEditStatements.EditCreateEdgeRequest map(IMatch match, FixCreateEdgeStatement fixStatement, IndexedEMFLoader emfLoader) {
        ModelServerEditStatements.Node protoFromNode;
        ModelServerEditStatements.Node protoToNode;

        if (fixStatement.isFromNameIsTemp()) {
            protoFromNode = ModelServerEditStatements.Node.newBuilder().setTempId(fixStatement.getFromPatternNodeName()).build();
        } else {
            SmartObject fromNode = (SmartObject) match.get(fixStatement.getFromPatternNodeName());
            int fromNodeId = emfLoader.getNodeId(fromNode);
            protoFromNode = ModelServerEditStatements.Node.newBuilder().setNodeId(fromNodeId).build();
        }

        if (fixStatement.isToNameIsTemp()) {
            protoToNode = ModelServerEditStatements.Node.newBuilder().setTempId(fixStatement.getToPatternNodeName()).build();
        } else {
            SmartObject toNode = (SmartObject) match.get(fixStatement.getToPatternNodeName());
            int toNodeId = emfLoader.getNodeId(toNode);
            protoToNode = ModelServerEditStatements.Node.newBuilder().setNodeId(toNodeId).build();
        }


        return ModelServerEditStatements.EditCreateEdgeRequest.newBuilder()
                .setStartNode(protoFromNode)
                .setTargetNode(protoToNode)
                .setReferenceName(fixStatement.getReferenceName())
                .build();
    }

    public static ModelServerEditStatements.EditCreateNodeRequest map(FixCreateNodeStatement fixStatement) {
        List<ModelServerEditStatements.EditCreateNodeAttributeAssignment> protoAssignments = fixStatement.getAttributeAssignments().stream().map(ProtoMapper::map).toList();

        return ModelServerEditStatements.EditCreateNodeRequest.newBuilder()
                .setTempId(fixStatement.getTempNodeName())
                .setNodeType(fixStatement.getNodeType())
                .addAllAssignments(protoAssignments)
                .build();
    }

    public static ModelServerEditStatements.EditCreateNodeAttributeAssignment map(AttributeAssignment attributeAssignment) {
        return ModelServerEditStatements.EditCreateNodeAttributeAssignment.newBuilder()
                .setAttributeName(attributeAssignment.getAttributeName())
                .setAttributeValue(attributeAssignment.getAttributeValue())
                .build();
    }

    public static ModelServerEditStatements.EditSetAttributeRequest map(IMatch match, FixSetStatement fixStatement, IndexedEMFLoader emfLoader) {
        SmartObject node = (SmartObject) match.get(fixStatement.getPatternNodeName());
        int nodeId = emfLoader.getNodeId(node);
        ModelServerEditStatements.Node protoNode = ModelServerEditStatements.Node.newBuilder().setNodeId(nodeId).build();

        return ModelServerEditStatements.EditSetAttributeRequest.newBuilder()
                .setNode(protoNode)
                .setAttributeName(fixStatement.getAttributeName())
                .setAttributeValue(fixStatement.getAttributeValue())
                .build();
    }
}
