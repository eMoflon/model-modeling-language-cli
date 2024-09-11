package de.nexus.expr;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PrimaryExpressionEntityDeserializerTest {

    PrimaryExpressionEntityDeserializer deserializer;

    @BeforeEach
    void setUp() {
        deserializer = new PrimaryExpressionEntityDeserializer();
    }

    @Test
    void deserializeStringPrimary() {
        JsonElement jsonElement = JsonParser.parseString(
                """
                        {
                            "valueType": "string",
                            "isAttribute": false,
                            "isEnumLiteral": false,
                            "isPatternDeclarationReference": false,
                            "value": "Hello World",
                            "className": "",
                            "elementName": "",
                            "nodeId": ""
                        }  \s
                        """
        );

        PrimaryExpressionEntity expr = this.deserializer.deserialize(jsonElement, PrimaryExpressionEntity.class, null);
        assertEquals(PrimaryExpressionEntityType.STRING, expr.getType());
        assertInstanceOf(PrimitivePrimaryExpressionEntity.class, expr);

        PrimitivePrimaryExpressionEntity<?> primitivePrimaryExpr = (PrimitivePrimaryExpressionEntity<?>) expr;
        assertEquals("Hello World", primitivePrimaryExpr.getValue());
    }

    @Test
    void deserializeIntegerPrimary() {
        JsonElement jsonElement = JsonParser.parseString(
                """
                        {
                            "valueType": "number",
                            "isAttribute": false,
                            "isEnumLiteral": false,
                            "isPatternDeclarationReference": false,
                            "value": 42,
                            "className": "",
                            "elementName": "",
                            "nodeId": ""
                        }  \s
                        """
        );

        PrimaryExpressionEntity expr = this.deserializer.deserialize(jsonElement, PrimaryExpressionEntity.class, null);
        assertEquals(PrimaryExpressionEntityType.INTEGER, expr.getType());
        assertInstanceOf(PrimitivePrimaryExpressionEntity.class, expr);

        PrimitivePrimaryExpressionEntity<?> primitivePrimaryExpr = (PrimitivePrimaryExpressionEntity<?>) expr;
        assertEquals(42, primitivePrimaryExpr.getValue());
    }

    @Test
    void deserializeDoublePrimary() {
        JsonElement jsonElement = JsonParser.parseString(
                """
                        {
                            "valueType": "number",
                            "isAttribute": false,
                            "isEnumLiteral": false,
                            "isPatternDeclarationReference": false,
                            "value": 4.2,
                            "className": "",
                            "elementName": "",
                            "nodeId": ""
                        }  \s
                        """
        );

        PrimaryExpressionEntity expr = this.deserializer.deserialize(jsonElement, PrimaryExpressionEntity.class, null);
        assertEquals(PrimaryExpressionEntityType.DOUBLE, expr.getType());
        assertInstanceOf(PrimitivePrimaryExpressionEntity.class, expr);

        PrimitivePrimaryExpressionEntity<?> primitivePrimaryExpr = (PrimitivePrimaryExpressionEntity<?>) expr;
        assertEquals(4.2, primitivePrimaryExpr.getValue());
    }

    @Test
    void deserializeBooleanPrimary() {
        JsonElement jsonElement = JsonParser.parseString(
                """
                        {
                            "valueType": "boolean",
                            "isAttribute": false,
                            "isEnumLiteral": false,
                            "isPatternDeclarationReference": false,
                            "value": true,
                            "className": "",
                            "elementName": "",
                            "nodeId": ""
                        }  \s
                        """
        );

        PrimaryExpressionEntity expr = this.deserializer.deserialize(jsonElement, PrimaryExpressionEntity.class, null);
        assertEquals(PrimaryExpressionEntityType.BOOLEAN, expr.getType());
        assertInstanceOf(PrimitivePrimaryExpressionEntity.class, expr);

        PrimitivePrimaryExpressionEntity<?> primitivePrimaryExpr = (PrimitivePrimaryExpressionEntity<?>) expr;
        assertEquals(true, primitivePrimaryExpr.getValue());
    }

    @Test
    void deserializeEnumPrimary() {
        JsonElement jsonElement = JsonParser.parseString(
                """
                        {
                            "valueType": "string",
                            "isAttribute": false,
                            "isEnumLiteral": true,
                            "isPatternDeclarationReference": false,
                            "value": "TestLit",
                            "className": "",
                            "elementName": "",
                            "nodeId": ""
                        }  \s
                        """
        );

        PrimaryExpressionEntity expr = this.deserializer.deserialize(jsonElement, PrimaryExpressionEntity.class, null);
        assertEquals(PrimaryExpressionEntityType.ENUM_VALUE, expr.getType());
        assertInstanceOf(EnumValuePrimaryExpressionEntity.class, expr);

        EnumValuePrimaryExpressionEntity enumPrimaryExpr = (EnumValuePrimaryExpressionEntity) expr;
        assertEquals("TestLit", enumPrimaryExpr.getValue());
    }

    @Test
    void deserializePatternPrimary() {
        JsonElement jsonElement = JsonParser.parseString(
                """
                        {
                            "valueType": "string",
                            "isAttribute": false,
                            "isEnumLiteral": false,
                            "isPatternDeclarationReference": true,
                            "value": "TestPattern",
                            "className": "",
                            "elementName": "",
                            "nodeId": "test.node.id"
                        }  \s
                        """
        );

        PrimaryExpressionEntity expr = this.deserializer.deserialize(jsonElement, PrimaryExpressionEntity.class, null);
        assertEquals(PrimaryExpressionEntityType.PATTERN_INVOCATION, expr.getType());
        assertInstanceOf(PatternPrimaryExpressionEntity.class, expr);

        PatternPrimaryExpressionEntity patternPrimaryExpr = (PatternPrimaryExpressionEntity) expr;
        assertEquals("TestPattern", patternPrimaryExpr.getPatternName());
        assertEquals("test.node.id", patternPrimaryExpr.getNodeId());
    }

    @Test
    void deserializeAttributePrimary() {
        JsonElement jsonElement = JsonParser.parseString(
                """
                        {
                            "valueType": "string",
                            "isAttribute": true,
                            "isEnumLiteral": false,
                            "isPatternDeclarationReference": false,
                            "value": "",
                            "className": "TestClass",
                            "elementName": "TestAttr",
                            "nodeId": "test.node.id"
                        }  \s
                        """
        );

        PrimaryExpressionEntity expr = this.deserializer.deserialize(jsonElement, PrimaryExpressionEntity.class, null);
        assertEquals(PrimaryExpressionEntityType.ATTRIBUTE, expr.getType());
        assertInstanceOf(AttributePrimaryExpressionEntity.class, expr);

        AttributePrimaryExpressionEntity attributePrimaryExpr = (AttributePrimaryExpressionEntity) expr;
        assertEquals("TestClass", attributePrimaryExpr.getClassName());
        assertEquals("TestAttr", attributePrimaryExpr.getElementName());
        assertEquals("test.node.id", attributePrimaryExpr.getNodeId());
    }

    @Test
    void deserializePrimaryWithUnknownType() {
        JsonElement jsonElement = JsonParser.parseString(
                """
                        {
                            "valueType": "UNKNOWNTYPE",
                            "isAttribute": false,
                            "isEnumLiteral": false,
                            "isPatternDeclarationReference": false,
                            "value": "Hello World",
                            "className": "",
                            "elementName": "",
                            "nodeId": ""
                        }  \s
                        """
        );

        Throwable exception = assertThrows(RuntimeException.class, () -> this.deserializer.deserialize(jsonElement, PrimaryExpressionEntity.class, null));
        assertEquals("Unknown value type: UNKNOWNTYPE", exception.getMessage());
    }
}