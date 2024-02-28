package de.nexus.modelserver;

import de.nexus.emfutils.EMFValueUtils;
import de.nexus.modelserver.proto.ModelServerEditStatements;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.emoflon.smartemf.runtime.SmartObject;

public class ModelEditProcessor {
    private final IndexedEMFLoader emfLoader;

    public ModelEditProcessor(IndexedEMFLoader emfLoader) {
        this.emfLoader = emfLoader;
    }

    public void process(ModelServerEditStatements.EditRequest edit, ModelEditVariableRegistry variableRegistry) {
        switch (edit.getRequestCase()) {
            case CREATEEDGEREQUEST -> this.process(edit.getCreateEdgeRequest(), variableRegistry);
            case CREATENODEREQUEST -> this.process(edit.getCreateNodeRequest(), variableRegistry);
            case DELETEEDGEREQUEST -> this.process(edit.getDeleteEdgeRequest(), variableRegistry);
            case DELETENODEREQUEST -> this.process(edit.getDeleteNodeRequest(), variableRegistry);
            case SETATTRIBUTEREQUEST -> this.process(edit.getSetAttributeRequest(), variableRegistry);
            case REQUEST_NOT_SET -> throw new IllegalArgumentException("EditRequest not set!");
        }
    }

    public void process(ModelServerEditStatements.EditRequest edit) {
        this.process(edit, new ModelEditVariableRegistry());
    }

    public void process(ModelServerEditStatements.EditChainRequest editChain) {
        ModelEditVariableRegistry variableRegistry = new ModelEditVariableRegistry();
        for (ModelServerEditStatements.EditRequest editRequest : editChain.getEditsList()) {
            this.process(editRequest, variableRegistry);
        }
    }

    private void process(ModelServerEditStatements.EditCreateEdgeRequest request, ModelEditVariableRegistry variableRegistry) {
        int fromNodeId = variableRegistry.getNodeId(request.getStartNode());
        SmartObject fromNode = this.emfLoader.getNode(fromNodeId);
        int toNodeId = variableRegistry.getNodeId(request.getTargetNode());
        SmartObject toNode = this.emfLoader.getNode(toNodeId);
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
    }

    private void process(ModelServerEditStatements.EditCreateNodeRequest request, ModelEditVariableRegistry variableRegistry) {

    }

    private void process(ModelServerEditStatements.EditDeleteEdgeRequest request, ModelEditVariableRegistry variableRegistry) {
        int fromNodeId = variableRegistry.getNodeId(request.getStartNode());
        SmartObject fromNode = this.emfLoader.getNode(fromNodeId);
        int toNodeId = variableRegistry.getNodeId(request.getTargetNode());
        SmartObject toNode = this.emfLoader.getNode(toNodeId);
        EReference reference = (EReference) fromNode.eClass().getEStructuralFeature(request.getReferenceName());
        if (reference.isMany()) {
            @SuppressWarnings("unchecked")
            EList<EObject> oldVals = (EList<EObject>) fromNode.eGet(reference);
            oldVals.remove(toNode);
        } else {
            fromNode.eUnset(reference);
        }
    }

    private void process(ModelServerEditStatements.EditDeleteNodeRequest request, ModelEditVariableRegistry variableRegistry) {

    }

    private void process(ModelServerEditStatements.EditSetAttributeRequest request, ModelEditVariableRegistry variableRegistry) {
        int nodeId = variableRegistry.getNodeId(request.getNode());
        SmartObject node = this.emfLoader.getNode(nodeId);
        EAttribute attribute = (EAttribute) node.eClass().getEStructuralFeature(request.getAttributeName());
        Object newValue = EMFValueUtils.mapVals(attribute.getEType(), request.getAttributeValue());
        node.eSet(attribute, newValue);
    }
}
