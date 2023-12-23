package de.nexus.mmlcli.generator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.nexus.mmlcli.entities.instance.GeneratorInstanceWrapper;
import de.nexus.mmlcli.entities.model.ModelEntity;

public class DeserializedDocument {
    private ModelEntity typegraph;
    private GeneratorInstanceWrapper instancegraph;

    public ModelEntity getTypegraph() {
        return this.typegraph;
    }

    public GeneratorInstanceWrapper getInstancegraph() {
        return this.instancegraph;
    }

    private void setTypegraph(ModelEntity typegraph) {
        this.typegraph = typegraph;
    }

    private void setInstancegraph(GeneratorInstanceWrapper instancegraph) {
        this.instancegraph = instancegraph;
    }

    public static DeserializedDocument build(String json) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        return gson.fromJson(json, DeserializedDocument.class);
    }

    public static DeserializedDocument build(ModelEntity model){
        DeserializedDocument doc = new DeserializedDocument();
        doc.setTypegraph(model);
        doc.setInstancegraph(new GeneratorInstanceWrapper());
        return doc;
    }
}
