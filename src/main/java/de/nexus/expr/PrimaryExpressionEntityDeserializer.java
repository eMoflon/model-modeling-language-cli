package de.nexus.expr;

import com.google.gson.*;

import java.lang.reflect.Type;

public class PrimaryExpressionEntityDeserializer implements JsonDeserializer<PrimaryExpressionEntity> {
    @Override
    public PrimaryExpressionEntity deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        String valueType = jsonObject.get("valueType").getAsString();
        boolean isAttribute = jsonObject.get("isAttribute").getAsBoolean();
        boolean isEnumLiteral = jsonObject.get("isEnumLiteral").getAsBoolean();
        boolean isPatternDeclarationReference = jsonObject.get("isPatternDeclarationReference").getAsBoolean();
        PrimaryExpressionEntityType primaryValueType = switch (valueType) {
            case "number" -> PrimaryExpressionEntityType.NUMBER;
            case "boolean" -> PrimaryExpressionEntityType.BOOLEAN;
            case "string" -> {
                if (isAttribute) {
                    yield PrimaryExpressionEntityType.ATTRIBUTE;
                } else if (isEnumLiteral) {
                    yield PrimaryExpressionEntityType.ENUM_VALUE;
                } else if (isPatternDeclarationReference) {
                    yield PrimaryExpressionEntityType.PATTERN_INVOCATION;
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

        return switch (primaryValueType) {
            case NUMBER -> {
                double valueAsDouble = value.getAsDouble();
                if (valueAsDouble % 1 == 0) {
                    yield new PrimitivePrimaryExpressionEntity<>(value.getAsInt(), PrimaryExpressionEntityType.INTEGER);
                }
                yield new PrimitivePrimaryExpressionEntity<>(valueAsDouble, PrimaryExpressionEntityType.DOUBLE);
            }
            case BOOLEAN -> new PrimitivePrimaryExpressionEntity<>(value.getAsBoolean(), primaryValueType);
            case STRING -> new PrimitivePrimaryExpressionEntity<>(value.getAsString(), primaryValueType);
            case ENUM_VALUE -> new EnumValuePrimaryExpressionEntity(className, value.getAsString());
            case ATTRIBUTE -> new AttributePrimaryExpressionEntity(className, elementName, nodeId);
            case PATTERN_INVOCATION -> new PatternPrimaryExpressionEntity(nodeId, value.getAsString());
            default -> throw new IllegalStateException("Unexpected value: " + primaryValueType);
        };
    }
}
