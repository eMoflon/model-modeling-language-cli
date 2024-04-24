package de.nexus.modelserver;

import de.nexus.emfutils.EMFValueUtils;
import de.nexus.modelserver.proto.ModelServerVisualization;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.emoflon.smartemf.runtime.SmartObject;

import java.util.*;

public class ModelVisualizer {
    private final IndexedEMFLoader emfLoader;

    public ModelVisualizer(IndexedEMFLoader emfLoader) {
        this.emfLoader = emfLoader;
    }

    public ModelServerVisualization.GetModelVisualizationResponse getModelVisualization(ModelServerVisualization.GetModelVisualizationRequest request) {
        HashSet<Integer> targetNodes;
        if (request.hasOptions() && !request.getOptions().getFilterNodesList().isEmpty()) {
            targetNodes = new HashSet<>(request.getOptions().getFilterNodesList());
        } else {
            targetNodes = new HashSet<>(this.emfLoader.getAllNodeIds());
        }
        return computeModelVisualization(targetNodes, request.getOptions());
    }

    private ModelServerVisualization.GetModelVisualizationResponse computeModelVisualization(Set<Integer> targetNodes, ModelServerVisualization.VisualizationRequestOptions options) {
        VisualizationStyleRegistry styleRegistry = new VisualizationStyleRegistry(options);

        ArrayList<ModelServerVisualization.VisualizationNode> nodes = new ArrayList<>();
        ArrayList<ModelServerVisualization.VisualizationEdge> edges = new ArrayList<>();


        for (int nodeId : targetNodes) {
            SmartObject node = this.emfLoader.getNode(nodeId);
            if (node == null) {
                throw new IllegalArgumentException(String.format("Tried to acces node with id (%d), but it does not exist!", nodeId));
            }

            VisualizationStyle style = styleRegistry.getStyle(nodeId);

            EList<EAttribute> eAttributes = node.eClass().getEAllAttributes();
            List<ModelServerVisualization.VisualizationNodeAttribute> attributes = eAttributes.stream()
                    .filter(eAttribute -> !this.emfLoader.isFeatureNodeId(eAttribute))
                    .map(eAttribute ->
                            ModelServerVisualization.VisualizationNodeAttribute.newBuilder()
                                    .setAttributeName(eAttribute.getName())
                                    .setAttributeValue(EMFValueUtils.requestValue(node, eAttribute).getAsString())
                                    .build()
                    ).toList();

            ModelServerVisualization.VisualizationNode vNode = ModelServerVisualization.VisualizationNode.newBuilder()
                    .setNodeId(nodeId)
                    .setNodeType(node.eClass().getName())
                    .addAllAttributes(attributes)
                    .setOptions(
                            ModelServerVisualization.VisualizationOptions.newBuilder()
                                    .setHighlight(style.isHighlighted())
                                    .build()
                    )
                    .build();

            nodes.add(vNode);

            node.eClass().getEAllReferences().stream()
                    .filter(x -> !x.getName().contains("_inverseTo_"))
                    .forEach(eReference -> {
                        if (eReference.isMany()) {
                            EList<SmartObject> targets = (EList<SmartObject>) node.eGet(eReference);
                            targets.forEach(target -> {
                                int targetId = this.emfLoader.getNodeId(target);
                                if (targetNodes.contains(targetId)) {
                                    ModelServerVisualization.VisualizationEdge vEdge = ModelServerVisualization.VisualizationEdge.newBuilder()
                                            .setFromNodeId(nodeId)
                                            .setToNodeId(this.emfLoader.getNodeId(target))
                                            .setEdgeName(eReference.getName())
                                            .build();
                                    edges.add(vEdge);
                                }
                            });
                        } else {
                            SmartObject target = (SmartObject) node.eGet(eReference);
                            if (target != null) {
                                int targetId = this.emfLoader.getNodeId(target);
                                if (targetNodes.contains(targetId)) {
                                    ModelServerVisualization.VisualizationEdge vEdge = ModelServerVisualization.VisualizationEdge.newBuilder()
                                            .setFromNodeId(nodeId)
                                            .setToNodeId(this.emfLoader.getNodeId(target))
                                            .setEdgeName(eReference.getName())
                                            .build();
                                    edges.add(vEdge);
                                }
                            }
                        }
                    });
        }

        return ModelServerVisualization.GetModelVisualizationResponse.newBuilder()
                .addAllNodes(nodes)
                .addAllEdges(edges)
                .build();
    }

    private static class VisualizationStyleRegistry {
        private final HashMap<Integer, VisualizationStyle> styles = new HashMap<>();

        public VisualizationStyleRegistry(ModelServerVisualization.VisualizationRequestOptions options) {
            options.getHighlightNodesList().forEach(nodeId ->
                    this.styles.put(nodeId, new VisualizationStyle(true))
            );
        }

        public VisualizationStyle getStyle(int nodeId) {
            return this.styles.getOrDefault(nodeId, new VisualizationStyle());
        }
    }

    private static class VisualizationStyle {
        private boolean highlighted;

        public VisualizationStyle() {
            this(false);
        }

        public VisualizationStyle(boolean highlighted) {
            this.highlighted = highlighted;
        }

        public boolean isHighlighted() {
            return highlighted;
        }

        public void setHighlighted(boolean highlighted) {
            this.highlighted = highlighted;
        }
    }
}
