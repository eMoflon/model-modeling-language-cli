package de.nexus.modelserver;

import de.nexus.emfutils.EMFResolverUtils;
import de.nexus.emfutils.EMFValueUtils;
import de.nexus.modelserver.proto.ModelServerEditStatements;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.*;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.emoflon.smartemf.runtime.SmartObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * The ModelEditProcessor performs model manipulations via an IndexedEMFLoader
 */
public class ModelEditProcessor {
    private final IndexedEMFLoader emfLoader;

    public ModelEditProcessor(IndexedEMFLoader emfLoader) {
        this.emfLoader = emfLoader;
    }

    /**
     * Perform a single EditRequest with a given ModelEditVariableRegistry
     *
     * @param edit             an EditRequest
     * @param variableRegistry the scope for temporary nodeIds
     * @return an EditResponse
     * @throws IllegalArgumentException for unknown EditRequest cases
     */
    public ModelServerEditStatements.EditResponse process(ModelServerEditStatements.EditRequest edit, ModelEditVariableRegistry variableRegistry) {
        return switch (edit.getRequestCase()) {
            case CREATEEDGEREQUEST -> this.process(edit.getCreateEdgeRequest(), variableRegistry);
            case CREATENODEREQUEST -> this.process(edit.getCreateNodeRequest(), variableRegistry);
            case DELETEEDGEREQUEST -> this.process(edit.getDeleteEdgeRequest(), variableRegistry);
            case DELETENODEREQUEST -> this.process(edit.getDeleteNodeRequest(), variableRegistry);
            case SETATTRIBUTEREQUEST -> this.process(edit.getSetAttributeRequest(), variableRegistry);
            case DELETEALLEDGESREQUEST -> this.process(edit.getDeleteAllEdgesRequest(), variableRegistry);
            case REQUEST_NOT_SET -> throw new IllegalArgumentException("EditRequest not set!");
        };
    }

    /**
     * Perform a single EditRequest in a new scope
     *
     * @param edit an EditRequest
     * @return an EditResponse
     */
    public ModelServerEditStatements.EditResponse process(ModelServerEditStatements.EditRequest edit) {
        return this.process(edit, new ModelEditVariableRegistry());
    }

    /**
     * Perform multiple EditRequest given as a EditChainRequest in a new common scope
     *
     * @param editChain an EditChainRequest
     * @return an EditChainResponse
     */
    public ModelServerEditStatements.EditChainResponse process(ModelServerEditStatements.EditChainRequest editChain) {
        ModelEditVariableRegistry variableRegistry = new ModelEditVariableRegistry();

        List<ModelServerEditStatements.EditResponse> responses = new ArrayList<>();

        for (ModelServerEditStatements.EditRequest request : editChain.getEditsList()) {
            ModelServerEditStatements.EditResponse response = this.process(request, variableRegistry);
            responses.add(response);
        }

        return ModelServerEditStatements.EditChainResponse.newBuilder().addAllEdits(responses).build();
    }

    /**
     * Perform an EditCreateEdgeRequest in a given scope.
     * <p>
     * Request the start and target node for the requested edge from the registry and create a new edge.
     *
     * @param request          an EditCreateEdgeRequest
     * @param variableRegistry the scope for temporary nodeIds
     * @return an EditResponse
     */
    private ModelServerEditStatements.EditResponse process(ModelServerEditStatements.EditCreateEdgeRequest request, ModelEditVariableRegistry variableRegistry) {
        try {
            SmartObject fromNode = this.getNode(request.getStartNode(), variableRegistry);
            SmartObject toNode = this.getNode(request.getTargetNode(), variableRegistry);
            EReference reference = getEReference(fromNode, request.getReferenceName());
            if (reference.isMany()) {
                @SuppressWarnings("unchecked")
                EList<EObject> oldVals = (EList<EObject>) fromNode.eGet(reference);
                if (!oldVals.contains(toNode)) {
                    oldVals.add(toNode);
                }
            } else {
                fromNode.eSet(reference, toNode);
            }
            return ModelServerEditStatements.EditResponse.newBuilder()
                    .setCreateEdgeResponse(
                            ModelServerEditStatements.EditCreateEdgeResponse.newBuilder()
                                    .setState(ModelServerEditStatements.EditState.SUCCESS)
                                    .build()
                    )
                    .build();
        } catch (IllegalArgumentException ex) {
            return ModelServerEditStatements.EditResponse.newBuilder()
                    .setCreateEdgeResponse(
                            ModelServerEditStatements.EditCreateEdgeResponse.newBuilder()
                                    .setState(ModelServerEditStatements.EditState.FAILURE)
                                    .setMessage(ex.getMessage())
                                    .build()
                    )
                    .build();
        }
    }

    /**
     * Perform an EditCreateNodeRequest in a given scope.
     * <p>
     * Creates a new node of the requested type, registers it in the IndexedEMFLoader and stores the temporary
     * nodeId in the ModelEditVariableRegistry.
     * The EditResponse contains the newly created permanent nodeId.
     *
     * @param request          an EditCreateEdgeRequest
     * @param variableRegistry the scope for temporary nodeIds
     * @return an EditResponse
     */
    private ModelServerEditStatements.EditResponse process(ModelServerEditStatements.EditCreateNodeRequest request, ModelEditVariableRegistry variableRegistry) {
        try {
            EPackage ePackage = this.emfLoader.getEPackage();
            EClass targetClass = EMFResolverUtils.getEClassByQualifiedName(ePackage, request.getNodeType());
            SmartObject newNode = (SmartObject) EcoreUtil.create(targetClass);
            int nodeId = this.emfLoader.initializeNode(newNode);

            variableRegistry.registerTemporaryVariable(request.getTempId(), nodeId);

            this.emfLoader.getResource().getContents().add(newNode);

            request.getAssignmentsList().forEach(assignment -> {
                EAttribute attr = (EAttribute) targetClass.getEStructuralFeature(assignment.getAttributeName());
                Object attrValue = EMFValueUtils.mapVals(attr.getEAttributeType(), assignment.getAttributeValue());
                if (attrValue == null) {
                    throw new IllegalArgumentException("Could not convert the attribute value to the specified type!");
                }
                newNode.eSet(attr, attrValue);
            });

            return ModelServerEditStatements.EditResponse.newBuilder()
                    .setCreateNodeResponse(
                            ModelServerEditStatements.EditCreateNodeResponse.newBuilder()
                                    .setState(ModelServerEditStatements.EditState.SUCCESS)
                                    .setCreatedNodeId(nodeId)
                                    .build()
                    )
                    .build();
        } catch (IllegalArgumentException ex) {
            return ModelServerEditStatements.EditResponse.newBuilder()
                    .setCreateNodeResponse(
                            ModelServerEditStatements.EditCreateNodeResponse.newBuilder()
                                    .setState(ModelServerEditStatements.EditState.FAILURE)
                                    .setMessage(ex.getMessage())
                                    .build()
                    )
                    .build();
        }
    }

    /**
     * Perform an EditDeleteEdgeRequest in a given scope.
     * <p>
     * Request the start and target node for the requested edge from the registry and removes the edge.
     *
     * @param request          an EditDeleteEdgeRequest
     * @param variableRegistry the scope for temporary nodeIds
     * @return an EditResponse
     */
    private ModelServerEditStatements.EditResponse process(ModelServerEditStatements.EditDeleteEdgeRequest request, ModelEditVariableRegistry variableRegistry) {
        try {
            SmartObject fromNode = this.getNode(request.getStartNode(), variableRegistry);
            SmartObject toNode = this.getNode(request.getTargetNode(), variableRegistry);
            EReference reference = getEReference(fromNode, request.getReferenceName());
            if (reference.isMany()) {
                @SuppressWarnings("unchecked")
                EList<EObject> oldVals = (EList<EObject>) fromNode.eGet(reference);
                oldVals.remove(toNode);
            } else {
                fromNode.eUnset(reference);
            }
            return ModelServerEditStatements.EditResponse.newBuilder()
                    .setDeleteEdgeResponse(
                            ModelServerEditStatements.EditDeleteEdgeResponse.newBuilder()
                                    .setState(ModelServerEditStatements.EditState.SUCCESS)
                                    .build()
                    )
                    .build();
        } catch (IllegalArgumentException ex) {
            return ModelServerEditStatements.EditResponse.newBuilder()
                    .setDeleteEdgeResponse(
                            ModelServerEditStatements.EditDeleteEdgeResponse.newBuilder()
                                    .setState(ModelServerEditStatements.EditState.FAILURE)
                                    .setMessage(ex.getMessage())
                                    .build()
                    )
                    .build();
        }
    }

    /**
     * Perform an EditDeleteAllEdgesRequest in a given scope.
     * <p>
     * Request the start node and delete all edges of a given type.
     *
     * @param request          an EditDeleteallEdgesRequest
     * @param variableRegistry the scope for temporary nodeIds
     * @return an EditResponse
     */
    private ModelServerEditStatements.EditResponse process(ModelServerEditStatements.EditDeleteAllEdgesRequest request, ModelEditVariableRegistry variableRegistry) {
        try {
            SmartObject fromNode = this.getNode(request.getStartNode(), variableRegistry);
            EReference reference = getEReference(fromNode, request.getReferenceName());
            List<ModelServerEditStatements.Node> deletedTargets = new ArrayList<>();
            if (reference.isMany()) {
                @SuppressWarnings("unchecked")
                EList<SmartObject> oldVals = (EList<SmartObject>) fromNode.eGet(reference);
                deletedTargets.addAll(oldVals.stream().map(this::getNode).toList());
                oldVals.clear();
            } else {
                SmartObject target = (SmartObject) fromNode.eGet(reference);
                deletedTargets.add(this.getNode(target));
                fromNode.eUnset(reference);
            }
            return ModelServerEditStatements.EditResponse.newBuilder()
                    .setDeleteAllEdgesResponse(
                            ModelServerEditStatements.EditDeleteAllEdgesResponse.newBuilder()
                                    .setState(ModelServerEditStatements.EditState.SUCCESS)
                                    .addAllRemovedTargets(deletedTargets)
                                    .build()
                    )
                    .build();
        } catch (IllegalArgumentException ex) {
            return ModelServerEditStatements.EditResponse.newBuilder()
                    .setDeleteAllEdgesResponse(
                            ModelServerEditStatements.EditDeleteAllEdgesResponse.newBuilder()
                                    .setState(ModelServerEditStatements.EditState.FAILURE)
                                    .setMessage(ex.getMessage())
                                    .build()
                    )
                    .build();
        }
    }

    /**
     * Perform an EditDeleteNodeRequest in a given scope.
     * <p>
     * Request a node and delete it from the model and the IndexedEMFLoader.
     * The EditResponse contains a list of all implicitly removed edges.
     *
     * @param request          an EditDeleteNodeRequest
     * @param variableRegistry the scope for temporary nodeIds
     * @return an EditResponse
     */
    private ModelServerEditStatements.EditResponse process(ModelServerEditStatements.EditDeleteNodeRequest request, ModelEditVariableRegistry variableRegistry) {
        try {
            SmartObject node = this.getNode(request.getNode(), variableRegistry);

            List<ModelServerEditStatements.ImplicitlyRemovedEdge> implicitlyRemovedEdges = deleteNode_internal(node);
            node.resetContainment();

            this.emfLoader.unregisterNode(node);

            return ModelServerEditStatements.EditResponse.newBuilder()
                    .setDeleteNodeResponse(
                            ModelServerEditStatements.EditDeleteNodeResponse.newBuilder()
                                    .setState(ModelServerEditStatements.EditState.SUCCESS)
                                    .addAllRemovedEdges(implicitlyRemovedEdges)
                                    .build()
                    )
                    .build();
        } catch (IllegalArgumentException ex) {
            return ModelServerEditStatements.EditResponse.newBuilder()
                    .setDeleteNodeResponse(
                            ModelServerEditStatements.EditDeleteNodeResponse.newBuilder()
                                    .setState(ModelServerEditStatements.EditState.FAILURE)
                                    .setMessage(ex.getMessage())
                                    .build()
                    )
                    .build();
        }

    }

    /**
     * Delete all outgoing edges for a given node.
     *
     * @param node a node
     * @return a list if all deleted edges
     */
    private List<ModelServerEditStatements.ImplicitlyRemovedEdge> deleteNode_internal(SmartObject node) {
        List<ModelServerEditStatements.ImplicitlyRemovedEdge> removedEdges = new ArrayList<>();

        for (EReference ref : node.eClass().getEAllReferences()) {
            Object value = node.eGet(ref);

            if (ref.isContainment()) {
                if (value != null) {
                    if (value instanceof EObject) {
                        List<ModelServerEditStatements.ImplicitlyRemovedEdge> recursiveRemovedEdges = deleteNode_internal((SmartObject) value);
                        removedEdges.addAll(recursiveRemovedEdges);
                    } else if (value instanceof Collection) {
                        //noinspection unchecked
                        ((Collection<SmartObject>) value).forEach(o -> {
                            List<ModelServerEditStatements.ImplicitlyRemovedEdge> recursiveRemovedEdges = deleteNode_internal((SmartObject) o);
                            removedEdges.addAll(recursiveRemovedEdges);
                        });
                    }
                }
            } else {
                int nodeId = this.emfLoader.getNodeId(node);
                if (ref.isMany()) {
                    for (Object nodeObject : (EList<Object>) node.eGet(ref)) {
                        SmartObject targetNode = (SmartObject) nodeObject;

                        int targetNodeId = this.emfLoader.getNodeId(targetNode);
                        ModelServerEditStatements.ImplicitlyRemovedEdge removedEdge = getRemovedEdge(nodeId, targetNodeId, ref);

                        removedEdges.add(removedEdge);
                    }
                } else {
                    SmartObject targetNode = (SmartObject) node.eGet(ref);
                    if (targetNode != null) {
                        int targetNodeId = this.emfLoader.getNodeId(targetNode);
                        ModelServerEditStatements.ImplicitlyRemovedEdge removedEdge = getRemovedEdge(nodeId, targetNodeId, ref);

                        removedEdges.add(removedEdge);
                    }
                }

                node.eUnset(ref);
            }
        }
        return removedEdges;
    }

    /**
     * Generate a ImplicitlyRemovedEdge proto object for an edge of a given type between two nodes
     *
     * @param node1 start node
     * @param node2 target node
     * @param ref   edge type
     * @return an ImplicitlyRemovedEdge object
     */
    private ModelServerEditStatements.ImplicitlyRemovedEdge getRemovedEdge(int node1, int node2, EReference ref) {
        boolean reversed = ref.getName().contains("_inverseTo_");

        ModelServerEditStatements.ImplicitlyRemovedEdgeType edgeType = !reversed ? ModelServerEditStatements.ImplicitlyRemovedEdgeType.OUTGOING_EDGE : ModelServerEditStatements.ImplicitlyRemovedEdgeType.INCOMING_EDGE;

        int fromNode = !reversed ? node1 : node2;
        int toNode = !reversed ? node2 : node1;

        return ModelServerEditStatements.ImplicitlyRemovedEdge.newBuilder()
                .setType(edgeType)
                .setFromNode(
                        ModelServerEditStatements.Node.newBuilder()
                                .setNodeId(fromNode)
                                .build()
                )
                .setToNode(
                        ModelServerEditStatements.Node.newBuilder()
                                .setNodeId(toNode)
                                .build()
                )
                .setReference(ref.getName())
                .build();
    }

    /**
     * Perform an EditSetAttributeRequest in a given scope.
     * <p>
     * Request the node and update the requested attribute
     *
     * @param request          an EditSetAttributeRequest
     * @param variableRegistry the scope for temporary nodeIds
     * @return an EditResponse
     * @throws IllegalArgumentException if no value is provided or its type is incorrect
     */
    private ModelServerEditStatements.EditResponse process(ModelServerEditStatements.EditSetAttributeRequest request, ModelEditVariableRegistry variableRegistry) {
        try {
            if (request.getUnsetAttributeValue()) {
                throw new IllegalArgumentException("Unable to process EditSetAttributeRequest with unset attribute value");
            }

            SmartObject node = this.getNode(request.getNode(), variableRegistry);
            EAttribute attribute = getEAttribute(node, request.getAttributeName());
            Object newValue = EMFValueUtils.mapVals(attribute.getEType(), request.getAttributeValue());
            if (newValue == null) {
                throw new IllegalArgumentException("Could not convert the attribute value to the specified type!");
            }
            node.eSet(attribute, newValue);

            return ModelServerEditStatements.EditResponse.newBuilder()
                    .setSetAttributeResponse(
                            ModelServerEditStatements.EditSetAttributeResponse.newBuilder()
                                    .setState(ModelServerEditStatements.EditState.SUCCESS)
                                    .build()
                    )
                    .build();
        } catch (IllegalArgumentException ex) {
            return ModelServerEditStatements.EditResponse.newBuilder()
                    .setSetAttributeResponse(
                            ModelServerEditStatements.EditSetAttributeResponse.newBuilder()
                                    .setState(ModelServerEditStatements.EditState.FAILURE)
                                    .setMessage(ex.getMessage())
                                    .build()
                    )
                    .build();
        }
    }

    /**
     * Request a node from the IndexedEMFLoader based on a Node proto object
     *
     * @param node             a proto Node object
     * @param variableRegistry the scope for temporary nodeIds
     * @return the requested node
     * @throws IllegalArgumentException if the nodeId could not be extracted or the nodeId is unknown
     */
    private SmartObject getNode(ModelServerEditStatements.Node node, ModelEditVariableRegistry variableRegistry) throws IllegalArgumentException {
        try {
            int nodeId = variableRegistry.getNodeId(node);
            return Objects.requireNonNull(this.emfLoader.getNode(nodeId));
        } catch (IllegalArgumentException ex) {
            throw ex;
        } catch (NullPointerException ex) {
            throw new IllegalArgumentException("Unable to locate SmartObject in IndexedEMFLoader!");
        }
    }

    /**
     * Get a proto Node object for a SmartObject
     *
     * @param node a SmartObject
     * @return the Node object
     */
    private ModelServerEditStatements.Node getNode(SmartObject node) {
        int nodeId = this.emfLoader.getNodeId(node);
        return ModelServerEditStatements.Node.newBuilder().setNodeId(nodeId).build();
    }

    /**
     * Get a EReference by name
     *
     * @param obj           an object
     * @param referenceName the reference name
     * @return the requested EReference
     * @throws IllegalArgumentException if the reference could not be found
     */
    private EReference getEReference(SmartObject obj, String referenceName) {
        EStructuralFeature structuralFeature = obj.eClass().getEStructuralFeature(referenceName);
        if (structuralFeature == null) {
            throw new IllegalArgumentException(String.format("Could not find reference called \"%s\"", referenceName));
        } else if (structuralFeature instanceof EReference ref) {
            return ref;
        } else {
            throw new IllegalArgumentException("Structural Feature is no Reference!");
        }
    }

    /**
     * Get a EAttribute by name
     *
     * @param obj          an object
     * @param attriuteName the attribute name
     * @return the requested EAttribute
     * @throws IllegalArgumentException if the attribute could not be found
     */
    private EAttribute getEAttribute(SmartObject obj, String attriuteName) {
        EStructuralFeature structuralFeature = obj.eClass().getEStructuralFeature(attriuteName);
        if (structuralFeature == null) {
            throw new IllegalArgumentException(String.format("Could not find attribute called \"%s\"", attriuteName));
        } else if (structuralFeature instanceof EAttribute attr) {
            return attr;
        } else {
            throw new IllegalArgumentException("Structural Feature is no Attribute!");
        }
    }
}
