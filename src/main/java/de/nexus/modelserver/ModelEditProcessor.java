package de.nexus.modelserver;

import de.nexus.emfutils.EMFValueUtils;
import de.nexus.modelserver.proto.ModelServerEditStatements;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.*;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.emoflon.smartemf.runtime.SmartObject;

import java.util.*;

public class ModelEditProcessor {
    private final IndexedEMFLoader emfLoader;

    public ModelEditProcessor(IndexedEMFLoader emfLoader) {
        this.emfLoader = emfLoader;
    }

    public ModelServerEditStatements.EditResponse process(ModelServerEditStatements.EditRequest edit, ModelEditVariableRegistry variableRegistry) {
        return switch (edit.getRequestCase()) {
            case CREATEEDGEREQUEST -> this.process(edit.getCreateEdgeRequest(), variableRegistry);
            case CREATENODEREQUEST -> this.process(edit.getCreateNodeRequest(), variableRegistry);
            case DELETEEDGEREQUEST -> this.process(edit.getDeleteEdgeRequest(), variableRegistry);
            case DELETENODEREQUEST -> this.process(edit.getDeleteNodeRequest(), variableRegistry);
            case SETATTRIBUTEREQUEST -> this.process(edit.getSetAttributeRequest(), variableRegistry);
            case REQUEST_NOT_SET -> throw new IllegalArgumentException("EditRequest not set!");
        };
    }

    public ModelServerEditStatements.EditResponse process(ModelServerEditStatements.EditRequest edit) {
        return this.process(edit, new ModelEditVariableRegistry());
    }

    public ModelServerEditStatements.EditChainResponse process(ModelServerEditStatements.EditChainRequest editChain) {
        ModelEditVariableRegistry variableRegistry = new ModelEditVariableRegistry();

        List<ModelServerEditStatements.EditResponse> responses = new ArrayList<>();

        for (ModelServerEditStatements.EditRequest request : editChain.getEditsList()) {
            ModelServerEditStatements.EditResponse response = this.process(request, variableRegistry);
            responses.add(response);
        }

        return ModelServerEditStatements.EditChainResponse.newBuilder().addAllEdits(responses).build();
    }

    private ModelServerEditStatements.EditResponse process(ModelServerEditStatements.EditCreateEdgeRequest request, ModelEditVariableRegistry variableRegistry) {
        try {
            SmartObject fromNode = this.getNode(request.getStartNode(), variableRegistry);
            SmartObject toNode = this.getNode(request.getTargetNode(), variableRegistry);
            EReference reference = (EReference) fromNode.eClass().getEStructuralFeature(request.getReferenceName());
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

    private ModelServerEditStatements.EditResponse process(ModelServerEditStatements.EditCreateNodeRequest request, ModelEditVariableRegistry variableRegistry) {
        try {
            EPackage ePackage = this.emfLoader.getEPackage();
            ArrayList<String> splittedQNameArr = new ArrayList<>(Arrays.asList(request.getNodeType().split("\\.")));
            if (splittedQNameArr.size() <= 1) {
                throw new IllegalArgumentException("Invalid qualified class name structure!");
            } else {
                EPackage currentPackage = ePackage;
                for (int i = 0; i < splittedQNameArr.size() - 1; i++) {
                    String currentName = splittedQNameArr.get(i);
                    Optional<EPackage> newCurrentPackage = currentPackage.getESubpackages().stream().filter(x -> x.getName().equals(currentName)).findFirst();
                    if (newCurrentPackage.isEmpty()) {
                        throw new IllegalArgumentException("Invalid qualified class name - could not resolve packages!");
                    } else {
                        currentPackage = newCurrentPackage.get();
                    }
                }
                String className = splittedQNameArr.get(splittedQNameArr.size() - 1);
                EClass targetClass = (EClass) currentPackage.getEClassifier(className);
                SmartObject newNode = (SmartObject) EcoreUtil.create(targetClass);
                int nodeId = this.emfLoader.initializeNode(newNode);

                variableRegistry.registerTemporaryVariable(request.getTempId(), nodeId);

                this.emfLoader.getResource().getContents().add(newNode);

                request.getAssignmentsList().forEach(assignment -> {
                    EAttribute attr = (EAttribute) targetClass.getEStructuralFeature(assignment.getAttributeName());
                    Object attrValue = EMFValueUtils.mapVals(attr.getEAttributeType(), assignment.getAttributeValue());
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
            }
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

    private ModelServerEditStatements.EditResponse process(ModelServerEditStatements.EditDeleteEdgeRequest request, ModelEditVariableRegistry variableRegistry) {
        try {
            SmartObject fromNode = this.getNode(request.getStartNode(), variableRegistry);
            SmartObject toNode = this.getNode(request.getTargetNode(), variableRegistry);
            EReference reference = (EReference) fromNode.eClass().getEStructuralFeature(request.getReferenceName());
            if (reference.isMany()) {
                @SuppressWarnings("unchecked")
                EList<EObject> oldVals = (EList<EObject>) fromNode.eGet(reference);
                oldVals.remove(toNode);
            } else {
                fromNode.eUnset(reference);
            }
            return ModelServerEditStatements.EditResponse.newBuilder()
                    .setDeleteNodeResponse(
                            ModelServerEditStatements.EditDeleteNodeResponse.newBuilder()
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

    private ModelServerEditStatements.EditResponse process(ModelServerEditStatements.EditDeleteNodeRequest request, ModelEditVariableRegistry variableRegistry) {
        try {
            SmartObject node = this.getNode(request.getNode(), variableRegistry);
            int nodeId = this.emfLoader.getNodeId(node);

            List<ModelServerEditStatements.ImplicitlyRemovedEdge> implicitlyRemovedEdges = new ArrayList<>();

            // Modified version of EcoreUtil.delete()
            EObject rootEObject = EcoreUtil.getRootContainer(node);
            Resource resource = rootEObject.eResource();

            Collection<EStructuralFeature.Setting> usages;
            if (resource == null) {
                usages = EcoreUtil.UsageCrossReferencer.find(node, rootEObject);
            } else {
                ResourceSet resourceSet = resource.getResourceSet();
                if (resourceSet == null) {
                    usages = EcoreUtil.UsageCrossReferencer.find(node, resource);
                } else {
                    usages = EcoreUtil.UsageCrossReferencer.find(node, resourceSet);
                }
            }

            for (EStructuralFeature.Setting setting : usages) {
                if (setting.getEStructuralFeature().isChangeable()) {
                    EcoreUtil.remove(setting, node);

                    // TODO: Add response for implicitly removed incoming edges

                    /*ModelServerEditStatements.ImplicitlyRemovedEdge removedEdge = ModelServerEditStatements.ImplicitlyRemovedEdge.newBuilder()
                            .setType(ModelServerEditStatements.ImplicitlyRemovedEdgeType.INCOMING_EDGE)
                            .setFromNode(
                                    ModelServerEditStatements.Node.newBuilder()
                                            .setNodeId()
                                            .build()
                            )
                            .setToNode(
                                    ModelServerEditStatements.Node.newBuilder()
                                            .setNodeId()
                                            .build()
                            )
                            .setReference(setting.getEStructuralFeature().getName())
                            .build();

                    implicitlyRemovedEdges.add(removedEdge);*/
                }
            }


            for (EReference outgoingReference : node.eClass().getEAllReferences()) {
                if (!outgoingReference.isContainment()) {
                    if (outgoingReference.isMany()) {
                        for (Object nodeObject : (EList<Object>) node.eGet(outgoingReference)) {
                            SmartObject outgoingTargetNode = (SmartObject) nodeObject;

                            ModelServerEditStatements.ImplicitlyRemovedEdge removedEdge = ModelServerEditStatements.ImplicitlyRemovedEdge.newBuilder()
                                    .setType(ModelServerEditStatements.ImplicitlyRemovedEdgeType.OUTGOING_EDGE)
                                    .setFromNode(
                                            ModelServerEditStatements.Node.newBuilder()
                                                    .setNodeId(nodeId)
                                                    .build()
                                    )
                                    .setToNode(
                                            ModelServerEditStatements.Node.newBuilder()
                                                    .setNodeId(this.emfLoader.getNodeId(outgoingTargetNode))
                                                    .build()
                                    )
                                    .setReference(outgoingReference.getName())
                                    .build();

                            implicitlyRemovedEdges.add(removedEdge);
                        }
                    } else {
                        SmartObject value = (SmartObject) node.eGet(outgoingReference);
                        if (value != null) {
                            ModelServerEditStatements.ImplicitlyRemovedEdge removedEdge = ModelServerEditStatements.ImplicitlyRemovedEdge.newBuilder()
                                    .setType(ModelServerEditStatements.ImplicitlyRemovedEdgeType.OUTGOING_EDGE)
                                    .setFromNode(
                                            ModelServerEditStatements.Node.newBuilder()
                                                    .setNodeId(nodeId)
                                                    .build()
                                    )
                                    .setToNode(
                                            ModelServerEditStatements.Node.newBuilder()
                                                    .setNodeId(this.emfLoader.getNodeId(value))
                                                    .build()
                                    )
                                    .setReference(outgoingReference.getName())
                                    .build();

                            implicitlyRemovedEdges.add(removedEdge);
                        }
                    }
                }
            }

            this.emfLoader.unregisterNode(node);
            EcoreUtil.remove(node);

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

    private ModelServerEditStatements.EditResponse process(ModelServerEditStatements.EditSetAttributeRequest request, ModelEditVariableRegistry variableRegistry) {
        try {
            SmartObject node = this.getNode(request.getNode(), variableRegistry);
            EAttribute attribute = (EAttribute) node.eClass().getEStructuralFeature(request.getAttributeName());
            Object newValue = EMFValueUtils.mapVals(attribute.getEType(), request.getAttributeValue());
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
}
