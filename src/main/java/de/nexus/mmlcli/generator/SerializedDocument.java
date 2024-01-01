package de.nexus.mmlcli.generator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.nexus.mmlcli.generator.diagnostic.DocumentDiagnostic;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class SerializedDocument {
    URI uri;
    String content;
    ArrayList<DocumentDiagnostic> diagnostics = new ArrayList<>();

    public DeserializedDocument getParsedGenerator() {
        return DeserializedDocument.build(this.content);
    }

    public SerializedDocument(URI uri, DeserializedDocument dDoc) {
        Gson gson = new Gson();
        this.content = gson.toJson(dDoc);
        this.uri = uri;
    }

    public String serialize() {
        Gson gson = new Gson();
        return gson.toJson(List.of(this));
    }

    public static SerializedDocument[] deserialize(String json) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        return gson.fromJson(json, SerializedDocument[].class);
    }

    public ArrayList<DocumentDiagnostic> getDiagnostics() {
        return diagnostics;
    }
}
