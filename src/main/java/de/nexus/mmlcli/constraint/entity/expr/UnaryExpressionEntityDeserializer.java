package de.nexus.mmlcli.constraint.entity.expr;

import com.google.gson.*;

import java.lang.reflect.Type;

public class UnaryExpressionEntityDeserializer implements JsonDeserializer<UnaryExpressionEntity> {
    @Override
    public UnaryExpressionEntity deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        String operator = jsonObject.get("operator").getAsString();
        UnaryOperator unaryOperator = switch (operator) {
            case "!" -> UnaryOperator.NEGATION;
            default -> throw new RuntimeException("Unknown unary operator: " + operator);
        };

        JsonObject exprObject = jsonObject.getAsJsonObject("expr");

        boolean exprIsBinary = jsonObject.get("exprIsBinary").getAsBoolean();
        boolean exprIsUnary = jsonObject.get("exprIsUnary").getAsBoolean();

        Type exprType = exprIsBinary ? BinaryExpressionEntity.class : exprIsUnary ? UnaryExpressionEntity.class : PrimaryExpressionEntity.class;

        ExpressionEntity exprExpr = jsonDeserializationContext.deserialize(exprObject, exprType);

        return new UnaryExpressionEntity(unaryOperator, exprExpr);
    }
}
