package de.nexus.mmlcli.constraint.entity.expr;

import com.google.gson.*;

import java.lang.reflect.Type;

public class PrimaryExpressionEntityDeserializer implements JsonDeserializer<PrimaryExpressionEntity<?>> {
    @Override
    public PrimaryExpressionEntity<?> deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        String valueType = jsonObject.get("valueType").getAsString();
        boolean isAttribute = jsonObject.get("isAttribute").getAsBoolean();
        boolean isEnumLiteral = jsonObject.get("isEnumLiteral").getAsBoolean();
        PrimaryExpressionEntityType primaryValueType = switch (valueType) {
            case "number" -> PrimaryExpressionEntityType.NUMBER;
            case "boolean" -> PrimaryExpressionEntityType.BOOLEAN;
            case "string" -> {
                if (isAttribute) {
                    yield PrimaryExpressionEntityType.ATTRIBUTE;
                } else if (isEnumLiteral) {
                    yield PrimaryExpressionEntityType.ENUM_VALUE;
                } else {
                    yield PrimaryExpressionEntityType.STRING;
                }
            }
            default -> throw new RuntimeException("Unknown value type: " + valueType);
        };

        JsonElement value = jsonObject.get("value");
        String className = jsonObject.get("className").getAsString();
        String elementName = jsonObject.get("elementName").getAsString();
        String nodeId = jsonObject.get("nodeId").getAsString();

        if (primaryValueType == PrimaryExpressionEntityType.NUMBER) {
            double valueAsDouble = value.getAsDouble();
            if (valueAsDouble % 1 == 0) {
                return new PrimaryExpressionEntity<>(value.getAsInt(), PrimaryExpressionEntityType.INTEGER, className, elementName, nodeId);
            }
            return new PrimaryExpressionEntity<>(valueAsDouble, PrimaryExpressionEntityType.DOUBLE, className, elementName, nodeId);
        } else if (primaryValueType == PrimaryExpressionEntityType.BOOLEAN) {
            return new PrimaryExpressionEntity<>(value.getAsBoolean(), primaryValueType, className, elementName, nodeId);
        } else {
            return new PrimaryExpressionEntity<>(value.getAsString(), primaryValueType, className, elementName, nodeId);
        }

    }
}
