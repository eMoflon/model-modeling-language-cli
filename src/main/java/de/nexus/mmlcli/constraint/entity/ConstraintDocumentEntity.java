package de.nexus.mmlcli.constraint.entity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;

public class ConstraintDocumentEntity {
    private final ArrayList<PatternEntity> patterns = new ArrayList<>();

    public ArrayList<PatternEntity> getPatterns() {
        return patterns;
    }

    public static ConstraintDocumentEntity build(String serialized) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        return gson.fromJson(serialized, ConstraintDocumentEntity.class);
    }
}
