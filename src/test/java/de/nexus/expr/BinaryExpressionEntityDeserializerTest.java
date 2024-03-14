package de.nexus.expr;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class BinaryExpressionEntityDeserializerTest {
    private BinaryExpressionEntityDeserializer deserializer;

    @BeforeEach
    void setUp() {
        this.deserializer = new BinaryExpressionEntityDeserializer();
    }

    private static Stream<Arguments> provideOperatorArguments() {
        return Stream.of(
                Arguments.of("==", BinaryOperator.EQUALS),
                Arguments.of("!=", BinaryOperator.NOT_EQUALS),
                Arguments.of("<", BinaryOperator.LESS_THAN),
                Arguments.of("<=", BinaryOperator.LESS_EQUAL_THAN),
                Arguments.of(">", BinaryOperator.GREATER_THAN),
                Arguments.of(">=", BinaryOperator.GREATER_EQUAL_THAN),
                Arguments.of("&&", BinaryOperator.LOGICAL_AND),
                Arguments.of("||", BinaryOperator.LOGICAL_OR)
        );
    }

    @ParameterizedTest
    @MethodSource("provideOperatorArguments")
    void deserializeWithPrimaryChild(String operatorString, BinaryOperator binaryOperator) {
        PrimitivePrimaryExpressionEntity<Boolean> expr1 = new PrimitivePrimaryExpressionEntity<>(true, PrimaryExpressionEntityType.BOOLEAN);
        PrimitivePrimaryExpressionEntity<Boolean> expr2 = new PrimitivePrimaryExpressionEntity<>(false, PrimaryExpressionEntityType.BOOLEAN);

        JsonElement jsonElement = JsonParser.parseString(
                String.format("""
                        {
                            "operator": "%s",
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
                        }  \s
                        """, operatorString)
        );

        JsonElement expr1JsonElement = JsonParser.parseString("""
                {
                    "value": true,
                    "valueType": "boolean",
                    "className": "",
                    "elementName": "",
                    "nodeId": "",
                    "isAttribute": false,
                    "isEnumLiteral": false,
                    "isPatternDeclarationReference": false
                }
                """
        );

        JsonElement expr2JsonElement = JsonParser.parseString("""
                {
                    "value": false,
                    "valueType": "boolean",
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
        Mockito.when(mockedContext.deserialize(expr1JsonElement, PrimaryExpressionEntity.class))
                .thenReturn(expr1);
        Mockito.when(mockedContext.deserialize(expr2JsonElement, PrimaryExpressionEntity.class))
                .thenReturn(expr2);

        BinaryExpressionEntity expr = this.deserializer.deserialize(jsonElement, BinaryExpressionEntity.class, mockedContext);
        assertEquals(binaryOperator, expr.getOperator());
        assertInstanceOf(PrimitivePrimaryExpressionEntity.class, expr.getLeft());
        assertInstanceOf(PrimitivePrimaryExpressionEntity.class, expr.getRight());

        PrimitivePrimaryExpressionEntity<?> leftExpr = (PrimitivePrimaryExpressionEntity<?>) expr.getLeft();
        assertEquals(PrimaryExpressionEntityType.BOOLEAN, leftExpr.getType());
        assertEquals(true, leftExpr.getValue());

        PrimitivePrimaryExpressionEntity<?> rightExpr = (PrimitivePrimaryExpressionEntity<?>) expr.getRight();
        assertEquals(PrimaryExpressionEntityType.BOOLEAN, rightExpr.getType());
        assertEquals(false, rightExpr.getValue());
    }

    @Test
    void deserializeWithBinaryChild() {
        PrimitivePrimaryExpressionEntity<Boolean> expr1 = new PrimitivePrimaryExpressionEntity<>(true, PrimaryExpressionEntityType.BOOLEAN);
        BinaryExpressionEntity expr2 = new BinaryExpressionEntity(BinaryOperator.LOGICAL_AND, new PrimitivePrimaryExpressionEntity<>(true, PrimaryExpressionEntityType.BOOLEAN), new PrimitivePrimaryExpressionEntity<>(false, PrimaryExpressionEntityType.BOOLEAN));

        JsonElement jsonElement = JsonParser.parseString(
                """
                        {
                            "operator": "||",
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

        JsonElement expr1JsonElement = JsonParser.parseString("""
                {
                    "value": true,
                    "valueType": "boolean",
                    "className": "",
                    "elementName": "",
                    "nodeId": "",
                    "isAttribute": false,
                    "isEnumLiteral": false,
                    "isPatternDeclarationReference": false
                }
                """
        );

        JsonElement expr2JsonElement = JsonParser.parseString("""
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
        Mockito.when(mockedContext.deserialize(expr1JsonElement, PrimaryExpressionEntity.class))
                .thenReturn(expr1);
        Mockito.when(mockedContext.deserialize(expr2JsonElement, BinaryExpressionEntity.class))
                .thenReturn(expr2);

        BinaryExpressionEntity expr = this.deserializer.deserialize(jsonElement, BinaryExpressionEntity.class, mockedContext);
        assertEquals(BinaryOperator.LOGICAL_OR, expr.getOperator());
        assertInstanceOf(PrimitivePrimaryExpressionEntity.class, expr.getLeft());
        assertInstanceOf(BinaryExpressionEntity.class, expr.getRight());

        PrimitivePrimaryExpressionEntity<?> leftExpr = (PrimitivePrimaryExpressionEntity<?>) expr.getLeft();
        assertEquals(PrimaryExpressionEntityType.BOOLEAN, leftExpr.getType());
        assertEquals(true, leftExpr.getValue());

        BinaryExpressionEntity rightExpr = (BinaryExpressionEntity) expr.getRight();
        assertEquals(BinaryOperator.LOGICAL_AND, rightExpr.getOperator());
    }

    @Test
    void deserializeWithUnaryChild() {
        PrimitivePrimaryExpressionEntity<Boolean> expr1 = new PrimitivePrimaryExpressionEntity<>(true, PrimaryExpressionEntityType.BOOLEAN);
        UnaryExpressionEntity expr2 = new UnaryExpressionEntity(UnaryOperator.NEGATION, new PrimitivePrimaryExpressionEntity<>(true, PrimaryExpressionEntityType.BOOLEAN));

        JsonElement jsonElement = JsonParser.parseString(
                """
                        {
                            "operator": "||",
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

        JsonElement expr1JsonElement = JsonParser.parseString("""
                {
                    "value": true,
                    "valueType": "boolean",
                    "className": "",
                    "elementName": "",
                    "nodeId": "",
                    "isAttribute": false,
                    "isEnumLiteral": false,
                    "isPatternDeclarationReference": false
                }
                """
        );

        JsonElement expr2JsonElement = JsonParser.parseString("""
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
        Mockito.when(mockedContext.deserialize(expr1JsonElement, PrimaryExpressionEntity.class))
                .thenReturn(expr1);
        Mockito.when(mockedContext.deserialize(expr2JsonElement, UnaryExpressionEntity.class))
                .thenReturn(expr2);

        BinaryExpressionEntity expr = this.deserializer.deserialize(jsonElement, BinaryExpressionEntity.class, mockedContext);
        assertEquals(BinaryOperator.LOGICAL_OR, expr.getOperator());
        assertInstanceOf(PrimitivePrimaryExpressionEntity.class, expr.getLeft());
        assertInstanceOf(UnaryExpressionEntity.class, expr.getRight());

        PrimitivePrimaryExpressionEntity<?> leftExpr = (PrimitivePrimaryExpressionEntity<?>) expr.getLeft();
        assertEquals(PrimaryExpressionEntityType.BOOLEAN, leftExpr.getType());
        assertEquals(true, leftExpr.getValue());

        UnaryExpressionEntity rightExpr = (UnaryExpressionEntity) expr.getRight();
        assertEquals(UnaryOperator.NEGATION, rightExpr.getOperator());
    }

    @Test
    void deserializeWithInvalidOperator() {
        JsonElement jsonElement = JsonParser.parseString(
                """
                        {
                            "operator": "UNKNOWNOPERATOR",
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
                        }  \s
                        """
        );

        JsonDeserializationContext mockedContext = Mockito.mock(JsonDeserializationContext.class);

        Throwable exception = assertThrows(RuntimeException.class, () -> this.deserializer.deserialize(jsonElement, BinaryExpressionEntity.class, mockedContext));
        assertEquals("Unknown binary operator: UNKNOWNOPERATOR", exception.getMessage());
    }
}