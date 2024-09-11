package de.nexus.expr;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UnaryExpressionEntityDeserializerTest {
    UnaryExpressionEntityDeserializer deserializer;

    @BeforeEach
    void setUp() {
        deserializer = new UnaryExpressionEntityDeserializer();
    }

    @Test
    void deserializeNegatedPrimary() {
        PrimitivePrimaryExpressionEntity<Boolean> boolExpr = new PrimitivePrimaryExpressionEntity<>(true, PrimaryExpressionEntityType.BOOLEAN);

        JsonElement jsonElement = JsonParser.parseString(
                """
                        {
                        "operator": "!",
                            "expr": {
                                "expr": {
                                    "valueType": "boolean",
                                    "value": true,
                                    "className": "",
                                    "elementName": "",
                                    "nodeId": "",
                                    "isAttribute": false,
                                    "isEnumLiteral": false,
                                    "isPatternDeclarationReference": false
                                },
                                "isUnary": false,
                                "isBinary": false
                            }
                        }  \s
                        """
        );

        JsonElement exprJsonElement = JsonParser.parseString("""
                {
                    "valueType": "boolean",
                    "value": true,
                    "className": "",
                    "elementName": "",
                    "nodeId": "",
                    "isAttribute": false,
                    "isEnumLiteral": false,
                    "isPatternDeclarationReference": false
                }
                """
        );

        JsonDeserializationContext mockedContext = Mockito.mock(JsonDeserializationContext.class);
        Mockito.when(mockedContext.deserialize(exprJsonElement, PrimaryExpressionEntity.class))
                .thenReturn(boolExpr);

        UnaryExpressionEntity expr = this.deserializer.deserialize(jsonElement, UnaryExpressionEntity.class, mockedContext);
        assertEquals(UnaryOperator.NEGATION, expr.getOperator());
        assertInstanceOf(PrimitivePrimaryExpressionEntity.class, expr.getExpr());

        PrimitivePrimaryExpressionEntity<?> primitivePrimaryExpression = (PrimitivePrimaryExpressionEntity<?>) expr.getExpr();
        assertEquals(PrimaryExpressionEntityType.BOOLEAN, primitivePrimaryExpression.getType());
        assertEquals(true, primitivePrimaryExpression.getValue());
    }

    @Test
    void deserializeNegatedBinary() {
        PrimitivePrimaryExpressionEntity<Boolean> leftExpr = new PrimitivePrimaryExpressionEntity<>(true, PrimaryExpressionEntityType.BOOLEAN);
        PrimitivePrimaryExpressionEntity<Boolean> rightExpr = new PrimitivePrimaryExpressionEntity<>(false, PrimaryExpressionEntityType.BOOLEAN);
        BinaryExpressionEntity biExpr = new BinaryExpressionEntity(BinaryOperator.LOGICAL_AND, leftExpr, rightExpr);

        JsonElement jsonElement = JsonParser.parseString(
                """
                        {
                            "operator": "!",
                            "expr": {
                                "expr": {
                                    "operator": "&&",
                                    "left": {
                                        "expr": {
                                            "value": true,
                                            "valueType": "boolean",
                                            "className": "",
                                            "elementName": "",
                                            "nodeId": "",
                                            "isAttribute": false,
                                            "isEnumLiteral": false,
                                            "isPatternDeclarationReference": false
                                        },
                                        "isUnary": false,
                                        "isBinary": false
                                    },
                                    "right": {
                                        "expr": {
                                            "value": false,
                                            "valueType": "boolean",
                                            "className": "",
                                            "elementName": "",
                                            "nodeId": "",
                                            "isAttribute": false,
                                            "isEnumLiteral": false,
                                            "isPatternDeclarationReference": false
                                        },
                                        "isUnary": false,
                                        "isBinary": false
                                    }
                                },
                                "isUnary": false,
                                "isBinary": true
                            }
                        }  \s
                        """
        );

        JsonElement exprJsonElement = JsonParser.parseString("""
                {
                    "operator": "&&",
                    "left": {
                        "expr": {
                            "value": true,
                            "valueType": "boolean",
                            "className": "",
                            "elementName": "",
                            "nodeId": "",
                            "isAttribute": false,
                            "isEnumLiteral": false,
                            "isPatternDeclarationReference": false
                        },
                        "isUnary": false,
                        "isBinary": false
                    },
                    "right": {
                        "expr": {
                            "value": false,
                            "valueType": "boolean",
                            "className": "",
                            "elementName": "",
                            "nodeId": "",
                            "isAttribute": false,
                            "isEnumLiteral": false,
                            "isPatternDeclarationReference": false
                        },
                        "isUnary": false,
                        "isBinary": false
                    }
                }
                """
        );

        JsonDeserializationContext mockedContext = Mockito.mock(JsonDeserializationContext.class);
        Mockito.when(mockedContext.deserialize(exprJsonElement, BinaryExpressionEntity.class))
                .thenReturn(biExpr);

        UnaryExpressionEntity expr = this.deserializer.deserialize(jsonElement, UnaryExpressionEntity.class, mockedContext);
        assertEquals(UnaryOperator.NEGATION, expr.getOperator());
        assertInstanceOf(BinaryExpressionEntity.class, expr.getExpr());

        BinaryExpressionEntity binaryExpressionEntity = (BinaryExpressionEntity) expr.getExpr();
        assertEquals(BinaryOperator.LOGICAL_AND, binaryExpressionEntity.getOperator());
    }

    @Test
    void deserializeNegatedUnary() {
        PrimitivePrimaryExpressionEntity<Boolean> boolExpr = new PrimitivePrimaryExpressionEntity<>(true, PrimaryExpressionEntityType.BOOLEAN);
        UnaryExpressionEntity unaryExpr = new UnaryExpressionEntity(UnaryOperator.NEGATION, boolExpr);

        JsonElement jsonElement = JsonParser.parseString(
                """
                        {
                            "operator": "!",
                            "expr": {
                                "expr": {
                                    "operator": "!",
                                    "expr": {
                                        "expr": {
                                            "value": true,
                                            "valueType": "boolean",
                                            "className": "",
                                            "elementName": "",
                                            "nodeId": "",
                                            "isAttribute": false,
                                            "isEnumLiteral": false,
                                            "isPatternDeclarationReference": false
                                        },
                                        "isUnary": false,
                                        "isBinary": false
                                    }
                                },
                                "isUnary": true,
                                "isBinary": false
                            }
                        }  \s
                        """
        );

        JsonElement exprJsonElement = JsonParser.parseString("""
                {
                    "operator": "!",
                    "expr": {
                        "expr": {
                            "value": true,
                            "valueType": "boolean",
                            "className": "",
                            "elementName": "",
                            "nodeId": "",
                            "isAttribute": false,
                            "isEnumLiteral": false,
                            "isPatternDeclarationReference": false
                        },
                        "isUnary": false,
                        "isBinary": false
                    }
                }
                """
        );

        JsonDeserializationContext mockedContext = Mockito.mock(JsonDeserializationContext.class);
        Mockito.when(mockedContext.deserialize(exprJsonElement, UnaryExpressionEntity.class))
                .thenReturn(unaryExpr);

        UnaryExpressionEntity expr = this.deserializer.deserialize(jsonElement, UnaryExpressionEntity.class, mockedContext);
        assertEquals(UnaryOperator.NEGATION, expr.getOperator());
        assertInstanceOf(UnaryExpressionEntity.class, expr.getExpr());

        UnaryExpressionEntity unaryExpression = (UnaryExpressionEntity) expr.getExpr();
        assertEquals(UnaryOperator.NEGATION, unaryExpression.getOperator());
    }

    @Test
    void deserializeUnknownOperator() {
        JsonElement jsonElement = JsonParser.parseString(
                """
                        {
                        "operator": "UNKNOWNOPERATOR",
                            "expr": {
                                "expr": {
                                    "valueType": "boolean",
                                    "value": true,
                                    "className": "",
                                    "elementName": "",
                                    "nodeId": "",
                                    "isAttribute": false,
                                    "isEnumLiteral": false,
                                    "isPatternDeclarationReference": false
                                },
                                "isUnary": false,
                                "isBinary": false
                            }
                        }  \s
                        """
        );

        Throwable exception = assertThrows(RuntimeException.class, () -> this.deserializer.deserialize(jsonElement, UnaryExpressionEntity.class, null));
        assertEquals("Unknown unary operator: UNKNOWNOPERATOR", exception.getMessage());
    }
}