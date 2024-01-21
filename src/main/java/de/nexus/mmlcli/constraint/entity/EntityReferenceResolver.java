package de.nexus.mmlcli.constraint.entity;

import java.util.ArrayList;
import java.util.HashMap;

public class EntityReferenceResolver {
    private final HashMap<String, PatternEntity> patternIdToPatternMap = new HashMap<>();
    private final HashMap<String, PatternNodeEntity> nodeIdToNodeMap = new HashMap<>();
    private final ArrayList<UnresolvedObject<?>> unresolvedObjects = new ArrayList<>();

    EntityReferenceResolver(ConstraintDocumentEntity cDoc) {
        cDoc.getPatterns().forEach(patternEntity -> {
            this.patternIdToPatternMap.put(patternEntity.getPatternId(), patternEntity);
            patternEntity.getPac().forEach(inv -> this.unresolvedObjects.add(new UnresolvedObject<>(inv)));
            patternEntity.getNac().forEach(inv -> this.unresolvedObjects.add(new UnresolvedObject<>(inv)));
            patternEntity.getNodes().forEach(node -> this.nodeIdToNodeMap.put(node.getNodeId(), node));
            patternEntity.getEdges().forEach(edge -> this.unresolvedObjects.add(new UnresolvedObject<>(edge)));
        });

        this.unresolvedObjects.forEach(unresolvedObject -> unresolvedObject.resolve(this));
    }

    public static EntityReferenceResolver resolve(ConstraintDocumentEntity cDoc) {
        return new EntityReferenceResolver(cDoc);
    }

    public PatternEntity resolvePatternId(String id) {
        return this.patternIdToPatternMap.get(id);
    }

    public PatternNodeEntity resolveNodeId(String id) {
        return this.nodeIdToNodeMap.get(id);
    }
}

class UnresolvedObject<T> {
    private final T element;

    public UnresolvedObject(T element) {
        this.element = element;
    }

    public void resolve(EntityReferenceResolver resolver) {
        if (element instanceof EdgeEntity edgeEntity) {
            edgeEntity.setFromNode(resolver.resolveNodeId(edgeEntity.getFromId()));
            edgeEntity.setToNode(resolver.resolveNodeId(edgeEntity.getToId()));
        } else if (element instanceof SupportPatternInvocationEntity invocationEntity) {
            invocationEntity.setPattern(resolver.resolvePatternId(invocationEntity.getPatternId()));
            invocationEntity.getBindings().forEach(binding -> {
                binding.setPatternNode1(resolver.resolveNodeId(binding.getNode1()));
                binding.setPatternNode2(resolver.resolveNodeId(binding.getNode2()));
            });
        } else {
            throw new UnsupportedOperationException("Missing resolver for " + element.toString());
        }
    }
}
