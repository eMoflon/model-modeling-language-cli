package de.nexus.mmlcli.constraint.entity;

import com.google.gson.*;
import de.nexus.expr.*;

import java.lang.reflect.Type;

public class FixSetAttributeEntityDeserializer implements JsonDeserializer<FixSetAttributeEntity> {

    @Override
    public FixSetAttributeEntity deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        String patternNodeName = jsonObject.get("patternNodeName").getAsString();
        String attributeName = jsonObject.get("attributeName").getAsString();
        boolean customizationRequired = jsonObject.get("customizationRequired").getAsBoolean();
        JsonObject exprContainerObject = jsonObject.getAsJsonObject("attributeValue");

        if (exprContainerObject != null && !customizationRequired) {
            JsonObject exprObject = exprContainerObject.getAsJsonObject("expr");
            boolean isBinary = exprContainerObject.get("isBinary").getAsBoolean();
            boolean isUnary = exprContainerObject.get("isUnary").getAsBoolean();

            Type exprType = isBinary ? BinaryExpressionEntity.class : isUnary ? UnaryExpressionEntity.class : PrimaryExpressionEntity.class;

            ExpressionEntity expressionEntity = jsonDeserializationContext.deserialize(exprObject, exprType);


            return new FixSetAttributeEntity(patternNodeName, attributeName, customizationRequired, expressionEntity);
        } else if (exprContainerObject == null && !customizationRequired) {
            throw new IllegalStateException("ExpressionConstrainer is null but customizationRequired is false");
        }
        return new FixSetAttributeEntity(patternNodeName, attributeName, customizationRequired, new PrimitivePrimaryExpressionEntity<>("", PrimaryExpressionEntityType.STRING));
    }
}
