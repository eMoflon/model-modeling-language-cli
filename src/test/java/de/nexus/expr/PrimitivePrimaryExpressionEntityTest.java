package de.nexus.expr;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PrimitivePrimaryExpressionEntityTest {
    PrimitivePrimaryExpressionEntity<String> stringPrimaryExpr;
    PrimitivePrimaryExpressionEntity<Boolean> boolPrimaryExpr;
    PrimitivePrimaryExpressionEntity<Integer> intPrimaryExpr;
    PrimitivePrimaryExpressionEntity<Double> doublePrimaryExpr;

    @BeforeEach
    void setUp() {
        stringPrimaryExpr = new PrimitivePrimaryExpressionEntity<>("Test Value", PrimaryExpressionEntityType.STRING);
        boolPrimaryExpr = new PrimitivePrimaryExpressionEntity<>(true, PrimaryExpressionEntityType.BOOLEAN);
        intPrimaryExpr = new PrimitivePrimaryExpressionEntity<>(42, PrimaryExpressionEntityType.INTEGER);
        doublePrimaryExpr = new PrimitivePrimaryExpressionEntity<>(4.2, PrimaryExpressionEntityType.DOUBLE);
    }

    @Test
    void getValue() {
        assertAll(
                () -> assertEquals("Test Value", this.stringPrimaryExpr.getValue()),
                () -> assertEquals(true, this.boolPrimaryExpr.getValue()),
                () -> assertEquals(42, this.intPrimaryExpr.getValue()),
                () -> assertEquals(4.2, this.doublePrimaryExpr.getValue())
        );
    }

    @Test
    void getAsString() {
        assertAll(
                () -> assertEquals("\"Test Value\"", this.stringPrimaryExpr.getAsString()),
                () -> assertEquals("true", this.boolPrimaryExpr.getAsString()),
                () -> assertEquals("42", this.intPrimaryExpr.getAsString()),
                () -> assertEquals("4.2", this.doublePrimaryExpr.getAsString())
        );
    }

    @Test
    void getType() {
        assertAll(
                () -> assertEquals(PrimaryExpressionEntityType.STRING, this.stringPrimaryExpr.getType()),
                () -> assertEquals(PrimaryExpressionEntityType.BOOLEAN, this.boolPrimaryExpr.getType()),
                () -> assertEquals(PrimaryExpressionEntityType.INTEGER, this.intPrimaryExpr.getType()),
                () -> assertEquals(PrimaryExpressionEntityType.DOUBLE, this.doublePrimaryExpr.getType())
        );
    }

    @Test
    void toJavaCode() {
        assertAll(
                () -> assertEquals("new PrimitivePrimaryExpressionEntity(\"Test Value\", STRING)", this.stringPrimaryExpr.toJavaCode()),
                () -> assertEquals("new PrimitivePrimaryExpressionEntity(true, BOOLEAN)", this.boolPrimaryExpr.toJavaCode()),
                () -> assertEquals("new PrimitivePrimaryExpressionEntity(42, INTEGER)", this.intPrimaryExpr.toJavaCode()),
                () -> assertEquals("new PrimitivePrimaryExpressionEntity(4.2, DOUBLE)", this.doublePrimaryExpr.toJavaCode())
        );
    }

    @Test
    void testToString() {
        assertAll(
                () -> assertEquals("{(PrimitiveValue) Test Value}", this.stringPrimaryExpr.toString()),
                () -> assertEquals("{(PrimitiveValue) true}", this.boolPrimaryExpr.toString()),
                () -> assertEquals("{(PrimitiveValue) 42}", this.intPrimaryExpr.toString()),
                () -> assertEquals("{(PrimitiveValue) 4.2}", this.doublePrimaryExpr.toString())
        );
    }
}