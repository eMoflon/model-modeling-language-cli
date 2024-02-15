package de.nexus.mmlcli.constraint.entity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.nexus.expr.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class ConstraintDocumentEntity {
    private final ArrayList<PatternEntity> patterns = new ArrayList<>();
    private volatile HashMap<String, PatternNodeEntity> id2PatternNode = new HashMap<>();
    private String packageName;
    private volatile HashSet<PatternNodeEntity> localNodes = new HashSet<>();
    private final ArrayList<ConstraintEntity> constraints = new ArrayList<>();

    public static ConstraintDocumentEntity build(String serialized) {
        GsonBuilder builder = new GsonBuilder();

        builder.registerTypeAdapter(BinaryExpressionEntity.class, new BinaryExpressionEntityDeserializer());
        builder.registerTypeAdapter(UnaryExpressionEntity.class, new UnaryExpressionEntityDeserializer());
        builder.registerTypeAdapter(PrimaryExpressionEntity.class, new PrimaryExpressionEntityDeserializer());
        builder.registerTypeAdapter(AttributeConstraintEntity.class, new AttributeConstraintEntityDeserializer());
        builder.registerTypeAdapter(ConstraintAssertionEntity.class, new ConstraintAssertionEntityDeserializer());
        builder.registerTypeAdapter(IFixStatementEntity.class, new FixStatementEntityDeserializer());

        Gson gson = builder.create();
        return gson.fromJson(serialized, ConstraintDocumentEntity.class);
    }

    public ArrayList<PatternEntity> getPatterns() {
        return patterns;
    }

    public String getPackageName() {
        return packageName;
    }

    public HashSet<PatternNodeEntity> getLocalNodes() {
        return localNodes;
    }

    public void setLocalNodes(HashSet<PatternNodeEntity> localNodes) {
        this.localNodes = localNodes;
    }

    public HashMap<String, PatternNodeEntity> getId2PatternNode() {
        return id2PatternNode;
    }

    public void setId2PatternNode(HashMap<String, PatternNodeEntity> id2PatternNode) {
        this.id2PatternNode = id2PatternNode;
    }

    public ArrayList<ConstraintEntity> getConstraints() {
        return constraints;
    }
}
