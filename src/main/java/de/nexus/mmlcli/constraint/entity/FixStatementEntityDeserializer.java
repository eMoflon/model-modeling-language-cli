package de.nexus.mmlcli.constraint.entity;

import com.google.gson.*;

import java.lang.reflect.Type;

public class FixStatementEntityDeserializer implements JsonDeserializer<IFixStatementEntity> {
    @Override
    public IFixStatementEntity deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        String statementType = jsonObject.get("type").getAsString();

        if (statementType.equals("INFO")) {
            return jsonDeserializationContext.deserialize(jsonObject, FixInfoStatementEntity.class);
        } else if (statementType.equals("DELETE_NODE")) {
            return jsonDeserializationContext.deserialize(jsonObject, FixDeleteNodeEntity.class);
        } else if (statementType.equals("DELETE_EDGE")) {
            return jsonDeserializationContext.deserialize(jsonObject, FixDeleteEdgeEntity.class);
        } else if (statementType.equals("CREATE_EDGE")) {
            return jsonDeserializationContext.deserialize(jsonObject, FixCreateEdgeEntity.class);
        } else if (statementType.equals("CREATE_NODE")) {
            return jsonDeserializationContext.deserialize(jsonObject, FixCreateNodeEntity.class);
        } else if (statementType.equals("SET")) {
            return jsonDeserializationContext.deserialize(jsonObject, FixSetAttributeEntity.class);
        }
        throw new UnsupportedOperationException("The following FixStatementType is not supported: " + statementType);
    }
}
