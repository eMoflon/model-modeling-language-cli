package de.nexus.mmlcli.generator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.net.URI;

public class SerializedDocument {
    URI uri;
    String content;

    public DeserializedDocument getParsedGenerator(){
        return DeserializedDocument.build(this.content);
    }

    public static SerializedDocument[] deserialize(String json) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        return gson.fromJson(json, SerializedDocument[].class);
    }
}
