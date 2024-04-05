package de.nexus.modelserver;

import de.nexus.modelserver.evaltree.EvalTree;
import de.nexus.modelserver.proto.ModelServerConstraints;
import de.nexus.modelserver.proto.ModelServerEditStatements;
import de.nexus.modelserver.proto.ModelServerPatterns;
import de.nexus.modelserver.runtime.EmptyMatch;
import de.nexus.modelserver.runtime.IMatch;
import org.eclipse.emf.ecore.EAttribute;
import org.emoflon.smartemf.runtime.SmartObject;

import java.util.List;
import java.util.Map;
import java.util.Objects;
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
                .setExpression(evalTree.getRoot().getExpression().toSimpleString())
                .setViolated(!evalTree.getState())
                .build();
    }

    public static ModelServerConstraints.ConstraintAssertion map(EvalTree evalTree, ModelServerConstraints.FixProposalContainer proposalContainer) {
        return ModelServerConstraints.ConstraintAssertion.newBuilder()
                .setExpression(evalTree.getRoot().getExpression().toSimpleString())
                .setProposalContainer(proposalContainer)
                .setViolated(!evalTree.getState())
                .build();
    }

    public static ModelServerConstraints.FixMatch map(IMatch match, List<FixContainer> variants, IndexedEMFLoader emfLoader) {
        boolean isEmptyMatch = match instanceof EmptyMatch;

        List<ModelServerConstraints.FixVariant> protoVariants = variants.stream().filter(x -> x.isEmptyMatchFix() == isEmptyMatch).map(x -> ProtoMapper.map(match, x, emfLoader)).toList();
        List<ModelServerConstraints.MatchNode> matchNodes = match.getParameters().stream().map(x -> ProtoMapper.map(x, emfLoader)).toList();

        return ModelServerConstraints.FixMatch.newBuilder()
                .addAllVariants(protoVariants)
                .addAllNodes(matchNodes)
                .setEmptyMatch(isEmptyMatch)
                .build();
    }

    public static ModelServerConstraints.MatchNode map(Map.Entry<String, Object> nodeEntry, IndexedEMFLoader emfLoader) {
        SmartObject nodeObj = (SmartObject) nodeEntry.getValue();

        List<ModelServerConstraints.MatchNodeAttribute> attributes = nodeObj.eClass().getEAllAttributes().stream().filter(x -> !emfLoader.isFeatureNodeId(x)).map(x -> ProtoMapper.map(nodeObj, x)).toList();

        return ModelServerConstraints.MatchNode.newBuilder()
                .setNodeId(emfLoader.getNodeId(nodeObj))
                .setNodeName(nodeEntry.getKey())
                .setNodeType(nodeObj.eClass().getName())
                .addAllNodeAttributes(attributes)
                .build();
    }

    public static ModelServerConstraints.MatchNodeAttribute map(SmartObject nodeObj, EAttribute attribute) {
        String attrValue = nodeObj.eGet(attribute).toString();

        return ModelServerConstraints.MatchNodeAttribute.newBuilder()
                .setAttributeName(attribute.getName())
                .setAttributeValue(attrValue)
                .build();
    }

    public static ModelServerConstraints.FixVariant map(IMatch match, FixContainer fixContainer, IndexedEMFLoader emfLoader) {
        List<ModelServerConstraints.FixStatement> statements = fixContainer.getStatements().stream().map(x -> ProtoMapper.map(match, x, emfLoader)).toList();

        return ModelServerConstraints.FixVariant.newBuilder()
                .setVariantTitle(fixContainer.getFixTitle())
                .addAllStatements(statements)
                .build();
    }

    public static ModelServerConstraints.FixStatement map(IMatch match, FixStatement fixStatement, IndexedEMFLoader emfLoader) {
        if (fixStatement instanceof FixInfoStatement infoStatement) {
            return ModelServerConstraints.FixStatement.newBuilder()
                    .setInfoStatement(
                            ModelServerConstraints.FixInfoStatement.newBuilder()
                                    .setMsg(infoStatement.getMsg(match))
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
        int fromNodeId = getNodeId(emfLoader, match, fixStatement.getFromPatternNodeName());
        int toNodeId = getNodeId(emfLoader, match, fixStatement.getToPatternNodeName());

        return ModelServerEditStatements.EditDeleteEdgeRequest.newBuilder()
                .setStartNode(ModelServerEditStatements.Node.newBuilder().setNodeId(fromNodeId).build())
                .setTargetNode(ModelServerEditStatements.Node.newBuilder().setNodeId(toNodeId).build())
                .setReferenceName(fixStatement.getReferenceName())
                .build();
    }

    public static ModelServerEditStatements.EditDeleteNodeRequest map(IMatch match, FixDeleteNodeStatement fixStatement, IndexedEMFLoader emfLoader) {
        int nodeId = getNodeId(emfLoader, match, fixStatement.getNodeName());

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
            int fromNodeId = getNodeId(emfLoader, match, fixStatement.getFromPatternNodeName());
            protoFromNode = ModelServerEditStatements.Node.newBuilder().setNodeId(fromNodeId).build();
        }

        if (fixStatement.isToNameIsTemp()) {
            protoToNode = ModelServerEditStatements.Node.newBuilder().setTempId(fixStatement.getToPatternNodeName()).build();
        } else {
            int toNodeId = getNodeId(emfLoader, match, fixStatement.getToPatternNodeName());
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
        int nodeId = getNodeId(emfLoader, match, fixStatement.getPatternNodeName());
        ModelServerEditStatements.Node protoNode = ModelServerEditStatements.Node.newBuilder().setNodeId(nodeId).build();

        return ModelServerEditStatements.EditSetAttributeRequest.newBuilder()
                .setNode(protoNode)
                .setAttributeName(fixStatement.getAttributeName())
                .setAttributeValue(fixStatement.getAttributeValue())
                .setUnsetAttributeValue(fixStatement.isCustomizationRequired())
                .build();
    }

    private static int getNodeId(IndexedEMFLoader emfLoader, IMatch match, String patternNodeName) {
        if (match instanceof EmptyMatch) {
            throw new RuntimeException("Unable to resolve node in empty match!");
        } else {
            try {
                SmartObject node = (SmartObject) Objects.requireNonNull(match.get(patternNodeName));
                return emfLoader.getNodeId(node);
            } catch (NullPointerException ex) {
                throw new RuntimeException("Unable to resolve nodeId!");
            }
        }
    }
}
