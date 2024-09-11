package de.nexus.mmlcli.constraint.entity;

import com.google.gson.*;
import de.nexus.expr.BinaryExpressionEntity;
import de.nexus.expr.ExpressionEntity;
import de.nexus.expr.PrimaryExpressionEntity;
import de.nexus.expr.UnaryExpressionEntity;

import java.lang.reflect.Type;

public class TemplateStringElementEntityDeserializer implements JsonDeserializer<TemplateStringElementEntity> {
    @Override
    public TemplateStringElementEntity deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        boolean isStringData = jsonObject.get("isString").getAsBoolean();

        if (isStringData) {
            String msg = jsonObject.get("msg").getAsString();
            return new TemplateStringElementEntity(msg);
        } else {
            JsonObject exprContainerObj = jsonObject.getAsJsonObject("data");
            JsonObject exprObject = exprContainerObj.getAsJsonObject("expr");
            boolean isBinary = exprContainerObj.get("isBinary").getAsBoolean();
            boolean isUnary = exprContainerObj.get("isUnary").getAsBoolean();

            Type exprType = isBinary ? BinaryExpressionEntity.class : isUnary ? UnaryExpressionEntity.class : PrimaryExpressionEntity.class;

            ExpressionEntity expressionEntity = jsonDeserializationContext.deserialize(exprObject, exprType);

            return new TemplateStringElementEntity(expressionEntity);
        }
    }
}
