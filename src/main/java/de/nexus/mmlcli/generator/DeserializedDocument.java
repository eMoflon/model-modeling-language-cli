package de.nexus.mmlcli.generator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.nexus.mmlcli.generator.entities.instance.GeneratorInstanceWrapper;
import de.nexus.mmlcli.generator.entities.model.ModelEntity;

public class DeserializedDocument {
    private ModelEntity typegraph;
    private GeneratorInstanceWrapper instancegraph;

    public ModelEntity getTypegraph() {
        return this.typegraph;
    }

    public GeneratorInstanceWrapper getInstancegraph() {
        return this.instancegraph;
    }

    public static DeserializedDocument build(String json) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        return gson.fromJson(json, DeserializedDocument.class);
    }
}
