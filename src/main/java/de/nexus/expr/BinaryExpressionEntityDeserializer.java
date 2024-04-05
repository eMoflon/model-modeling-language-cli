package de.nexus.expr;

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
            case "+" -> BinaryOperator.ADDITION;
            case "-" -> BinaryOperator.SUBTRACTION;
            case "*" -> BinaryOperator.MULTIPLICATION;
            case "/" -> BinaryOperator.DIVISION;
            case "%" -> BinaryOperator.MODULO;
            case "^" -> BinaryOperator.EXPONENTIATION;
            default -> throw new RuntimeException("Unknown binary operator: " + operator);
        };

        JsonObject leftContainerObject = jsonObject.getAsJsonObject("left");
        JsonObject rightContainerObject = jsonObject.getAsJsonObject("right");
        JsonObject leftObject = leftContainerObject.getAsJsonObject("expr");
        JsonObject rightObject = rightContainerObject.getAsJsonObject("expr");

        boolean leftIsBinary = leftContainerObject.get("isBinary").getAsBoolean();
        boolean leftIsUnary = leftContainerObject.get("isUnary").getAsBoolean();
        boolean rightIsBinary = rightContainerObject.get("isBinary").getAsBoolean();
        boolean rightIsUnary = rightContainerObject.get("isUnary").getAsBoolean();

        Type leftType = leftIsBinary ? BinaryExpressionEntity.class : leftIsUnary ? UnaryExpressionEntity.class : PrimaryExpressionEntity.class;
        Type rightType = rightIsBinary ? BinaryExpressionEntity.class : rightIsUnary ? UnaryExpressionEntity.class : PrimaryExpressionEntity.class;

        ExpressionEntity leftExpr = jsonDeserializationContext.deserialize(leftObject, leftType);
        ExpressionEntity rightExpr = jsonDeserializationContext.deserialize(rightObject, rightType);

        return new BinaryExpressionEntity(binaryOperator, leftExpr, rightExpr);
    }
}
