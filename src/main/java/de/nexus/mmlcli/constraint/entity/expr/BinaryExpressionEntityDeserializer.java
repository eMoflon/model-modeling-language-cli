package de.nexus.mmlcli.constraint.entity.expr;

import com.google.gson.*;

import java.lang.reflect.Type;

public class BinaryExpressionEntityDeserializer implements JsonDeserializer<BinaryExpressionEntity> {
    @Override
    public BinaryExpressionEntity deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        String operator = jsonObject.get("operator").getAsString();
        BinaryOperator binaryOperator = switch (operator) {
            case "==" -> BinaryOperator.EQUALS;
            case "!=" -> BinaryOperator.NOT_EQUALS;
            case "<" -> BinaryOperator.LESS_THAN;
            case "<=" -> BinaryOperator.LESS_EQUAL_THAN;
            case ">" -> BinaryOperator.GREATER_THAN;
            case ">=" -> BinaryOperator.GREATER_EQUAL_THAN;
            case "&&" -> BinaryOperator.LOGICAL_AND;
            case "||" -> BinaryOperator.LOGICAL_OR;
            default -> throw new RuntimeException("Unknown binary operator: " + operator);
        };

        JsonObject leftObject = jsonObject.getAsJsonObject("left");
        JsonObject rightObject = jsonObject.getAsJsonObject("right");

        boolean leftIsBinary = jsonObject.get("leftIsBinary").getAsBoolean();
        boolean rightIsBinary = jsonObject.get("rightIsBinary").getAsBoolean();

        Type leftType = leftIsBinary ? BinaryExpressionEntity.class : PrimaryExpressionEntity.class;
        Type rightType = rightIsBinary ? BinaryExpressionEntity.class : PrimaryExpressionEntity.class;

        ExpressionEntity leftExpr = jsonDeserializationContext.deserialize(leftObject, leftType);
        ExpressionEntity rightExpr = jsonDeserializationContext.deserialize(rightObject, rightType);

        return new BinaryExpressionEntity(binaryOperator, leftExpr, rightExpr);
    }
}
