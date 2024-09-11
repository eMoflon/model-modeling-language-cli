package de.nexus.modelserver;

import de.nexus.modelserver.proto.ModelServerEditStatements;

import java.util.HashMap;
import java.util.Map;

/**
 * A ModelEditVariableRegistry serves as a scope for EditRequests.
 * It can store mappings from temporary nodeIds to permanent nodeIds.
 */
public class ModelEditVariableRegistry {
    private final Map<String, Integer> temporaryVariableMap = new HashMap<>();

    public ModelEditVariableRegistry() {
    }

    /**
     * Register a new temporary nodeId with a permanent nodeId
     *
     * @param key the temporary nodeId
     * @param id  the permanent nodeId
     */
    public void registerTemporaryVariable(String key, int id) {
        this.temporaryVariableMap.put(key, id);
    }

    /**
     * Retrieve the internal nodeId of a node.
     * If the node contains a temporary nodeId, the temporary id is resolved
     * with the variable registry.
     *
     * @param node a node
     * @return the node id
     * @throws IllegalArgumentException if neither a nodeId nor a temporary nodeId or the
     *                                  temporary nodeId could not be resolved
     */
    public int getNodeId(ModelServerEditStatements.Node node) throws IllegalArgumentException {
        return switch (node.getNodeTypeCase()) {
            case NODEID -> node.getNodeId();
            case TEMPID -> {
                if (temporaryVariableMap.containsKey(node.getTempId())) {
                    yield temporaryVariableMap.get(node.getTempId());
                } else {
                    throw new IllegalArgumentException("Could not resolve unknown TemporaryVariableKey: " + node.getTempId());
                }
            }
            case NODETYPE_NOT_SET -> throw new IllegalArgumentException("Could not resolve node id: NodeTypeNotSet");
        };
    }
}
