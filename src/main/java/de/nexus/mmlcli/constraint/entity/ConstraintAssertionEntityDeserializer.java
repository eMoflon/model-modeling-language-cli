package de.nexus.mmlcli.constraint.entity;

import com.google.gson.*;
import de.nexus.expr.BinaryExpressionEntity;
import de.nexus.expr.ExpressionEntity;
import de.nexus.expr.PrimaryExpressionEntity;
import de.nexus.expr.UnaryExpressionEntity;

import java.lang.reflect.Type;

public class ConstraintAssertionEntityDeserializer implements JsonDeserializer<ConstraintAssertionEntity> {
    @Override
    public ConstraintAssertionEntity deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        JsonObject exprContainerObject = jsonObject.getAsJsonObject("expr");
        JsonObject exprObject = exprContainerObject.getAsJsonObject("expr");
        boolean isBinary = exprContainerObject.get("isBinary").getAsBoolean();
        boolean isUnary = exprContainerObject.get("isUnary").getAsBoolean();

        Type exprType = isBinary ? BinaryExpressionEntity.class : isUnary ? UnaryExpressionEntity.class : PrimaryExpressionEntity.class;

        ExpressionEntity expressionEntity = jsonDeserializationContext.deserialize(exprObject, exprType);

        return new ConstraintAssertionEntity(expressionEntity);
    }
}
