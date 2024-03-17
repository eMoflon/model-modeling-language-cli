package de.nexus.modelserver;

import de.nexus.modelserver.evaltree.EvalTree;
import de.nexus.modelserver.proto.ModelServerConstraints;
import de.nexus.modelserver.proto.ModelServerEditStatements;
import de.nexus.modelserver.proto.ModelServerPatterns;
import de.nexus.modelserver.runtime.IMatch;
import org.emoflon.smartemf.runtime.SmartObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
        if (constraint.getEvalTrees().size() != constraint.getProposals().size()) {
            throw new RuntimeException("#EvalTree does not match #Proposals - did you compute them first?");
        }
        List<ModelServerConstraints.ConstraintAssertion> assertions = IntStream.range(0, constraint.getEvalTrees().size()).mapToObj(idx -> {
            EvalTree evalTree = constraint.getEvalTrees().get(idx);
            Optional<ModelServerConstraints.FixProposalContainer> proposalContainer = constraint.getProposals().get(idx);

            return proposalContainer.map(container -> map(evalTree, container)).orElseGet(() -> map(evalTree));
        }).toList();

        return ModelServerConstraints.Constraint.newBuilder()
                .setTitle(constraint.getTitle())
                .setDescription(constraint.getDescription())
                .setName(constraint.getName())
                .setViolated(constraint.isViolated())
                .addAllAssertions(assertions)
                .build();
    }

    public static ModelServerConstraints.ConstraintAssertion map(EvalTree evalTree) {
        return ModelServerConstraints.ConstraintAssertion.newBuilder()
                .setExpression(evalTree.getRoot().toFormattedString(0))
                .setViolated(!evalTree.getState())
                .build();
    }

    public static ModelServerConstraints.ConstraintAssertion map(EvalTree evalTree, ModelServerConstraints.FixProposalContainer proposalContainer) {
        return ModelServerConstraints.ConstraintAssertion.newBuilder()
                .setExpression(evalTree.getRoot().toFormattedString(0))
                .setProposalContainer(proposalContainer)
                .setViolated(!evalTree.getState())
                .build();
    }

    public static ModelServerConstraints.FixMatch map(IMatch match, List<FixContainer> variants, IndexedEMFLoader emfLoader) {
        List<ModelServerConstraints.FixVariant> protoVariants = variants.stream().map(x -> ProtoMapper.map(match, x, emfLoader)).toList();

        return ModelServerConstraints.FixMatch.newBuilder()
                .addAllVariants(protoVariants)
                .build();
    }

    public static ModelServerConstraints.FixVariant map(IMatch match, FixContainer fixContainer, IndexedEMFLoader emfLoader) {
        List<ModelServerConstraints.FixInfoStatement> infoStatements = new ArrayList<>();
        List<ModelServerEditStatements.EditRequest> editStatements = new ArrayList<>();
        fixContainer.getStatements().forEach(x -> {
            if (x instanceof FixInfoStatement infoStatement) {
                infoStatements.add(map(match, infoStatement, emfLoader));
            } else {
                editStatements.add(map(match, x, emfLoader));
            }
        });

        return ModelServerConstraints.FixVariant.newBuilder()
                .addAllInfoStatements(infoStatements)
                .addAllEdits(editStatements)
                .build();
    }

    public static ModelServerConstraints.FixInfoStatement map(IMatch match, FixInfoStatement fixStatement, IndexedEMFLoader emfLoader) {
        return
                ModelServerConstraints.FixInfoStatement.newBuilder()
                        .setMsg(fixStatement.getMsg())
                        .build();
    }

    public static ModelServerEditStatements.EditRequest map(IMatch match, FixStatement fixStatement, IndexedEMFLoader emfLoader) {
        if (fixStatement instanceof FixDeleteEdgeStatement deleteEdgeStatement) {
            return ModelServerEditStatements.EditRequest.newBuilder()
                    .setDeleteEdgeRequest(ProtoMapper.map(match, deleteEdgeStatement, emfLoader))
                    .build();
        } else if (fixStatement instanceof FixDeleteNodeStatement deleteNodeStatement) {
            return ModelServerEditStatements.EditRequest.newBuilder()
                    .setDeleteNodeRequest(ProtoMapper.map(match, deleteNodeStatement, emfLoader))
                    .build();
        } else if (fixStatement instanceof FixCreateEdgeStatement createEdgeStatement) {
            return ModelServerEditStatements.EditRequest.newBuilder()
                    .setCreateEdgeRequest(ProtoMapper.map(match, createEdgeStatement, emfLoader))
                    .build();
        } else if (fixStatement instanceof FixCreateNodeStatement createNodeStatement) {
            return ModelServerEditStatements.EditRequest.newBuilder()
                    .setCreateNodeRequest(ProtoMapper.map(createNodeStatement))
                    .build();
        } else if (fixStatement instanceof FixSetStatement setStatement) {
            return ModelServerEditStatements.EditRequest.newBuilder()
                    .setSetAttributeRequest(ProtoMapper.map(match, setStatement, emfLoader))
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
