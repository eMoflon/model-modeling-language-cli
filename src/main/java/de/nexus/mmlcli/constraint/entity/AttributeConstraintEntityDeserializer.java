package de.nexus.mmlcli.constraint.entity;

import com.google.gson.*;
import de.nexus.mmlcli.constraint.entity.expr.BinaryExpressionEntity;
import de.nexus.mmlcli.constraint.entity.expr.ExpressionEntity;
import de.nexus.mmlcli.constraint.entity.expr.PrimaryExpressionEntity;

import java.lang.reflect.Type;

public class AttributeConstraintEntityDeserializer implements JsonDeserializer<AttributeConstraintEntity> {
    @Override
    public AttributeConstraintEntity deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        JsonObject exprObject = jsonObject.getAsJsonObject("expr");
        boolean isBinary = jsonObject.get("isBinary").getAsBoolean();

        Type exprType = isBinary ? BinaryExpressionEntity.class : PrimaryExpressionEntity.class;

        ExpressionEntity expressionEntity = jsonDeserializationContext.deserialize(exprObject, exprType);

        return new AttributeConstraintEntity(expressionEntity);
    }
}
