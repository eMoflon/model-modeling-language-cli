package de.nexus.modelserver;

import de.nexus.modelserver.proto.ModelServerEditStatements;

import java.util.HashMap;
import java.util.Map;

public class ModelEditVariableRegistry {
    private final Map<String, Integer> temporaryVariableMap = new HashMap<>();

    public ModelEditVariableRegistry() {
    }

    public void registerTemporaryVariable(String key, int id) {
        this.temporaryVariableMap.put(key, id);
    }

    public int getNodeId(ModelServerEditStatements.Node node) throws IllegalArgumentException{
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
