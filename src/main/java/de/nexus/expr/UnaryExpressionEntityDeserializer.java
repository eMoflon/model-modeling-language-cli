package de.nexus.expr;

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

        JsonObject exprContainerObject = jsonObject.getAsJsonObject("expr");
        JsonObject exprObject = exprContainerObject.getAsJsonObject("expr");

        boolean exprIsBinary = exprContainerObject.get("isBinary").getAsBoolean();
        boolean exprIsUnary = exprContainerObject.get("isUnary").getAsBoolean();

        Type exprType = exprIsBinary ? BinaryExpressionEntity.class : exprIsUnary ? UnaryExpressionEntity.class : PrimaryExpressionEntity.class;

        ExpressionEntity exprExpr = jsonDeserializationContext.deserialize(exprObject, exprType);

        return new UnaryExpressionEntity(unaryOperator, exprExpr);
    }
}
