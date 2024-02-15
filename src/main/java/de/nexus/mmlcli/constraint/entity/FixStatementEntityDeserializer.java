package de.nexus.mmlcli.constraint.entity;

import com.google.gson.*;

import java.lang.reflect.Type;

public class FixStatementEntityDeserializer implements JsonDeserializer<IFixStatementEntity> {
    @Override
    public IFixStatementEntity deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        String statementType = jsonObject.get("type").getAsString();

        if (statementType.equals("INFO")) {
            String msg = jsonObject.get("msg").getAsString();
            return new FixInfoStatementEntity(msg);
        }
        return null;
    }
}
