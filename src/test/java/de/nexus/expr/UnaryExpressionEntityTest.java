package de.nexus.expr;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class UnaryExpressionEntityTest {
    UnaryExpressionEntity negatedUnaryExpression;

    @BeforeEach
    void setUp() {
        negatedUnaryExpression = new UnaryExpressionEntity(UnaryOperator.NEGATION, new PrimitivePrimaryExpressionEntity<>(true, PrimaryExpressionEntityType.BOOLEAN));
    }

    @Test
    void getOperator() {
        assertEquals(UnaryOperator.NEGATION, this.negatedUnaryExpression.getOperator());
    }

    @Test
    void getExpr() {
        assertInstanceOf(PrimitivePrimaryExpressionEntity.class, this.negatedUnaryExpression.getExpr());
        assertEquals(PrimaryExpressionEntityType.BOOLEAN, ((PrimitivePrimaryExpressionEntity<?>) this.negatedUnaryExpression.getExpr()).getType());
        assertEquals(true, ((PrimitivePrimaryExpressionEntity<?>) this.negatedUnaryExpression.getExpr()).getValue());
    }

    @Test
    void toJavaCode() {
        assertEquals("new UnaryExpressionEntity(UnaryOperator.NEGATION, new PrimitivePrimaryExpressionEntity(true, BOOLEAN))", this.negatedUnaryExpression.toJavaCode());
    }

    @Test
    void testToString() {
        assertEquals("{[NEGATION] {(PrimitiveValue) true}}", this.negatedUnaryExpression.toString());
    }
}