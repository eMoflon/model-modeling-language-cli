package de.nexus.expr;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BinaryExpressionEntityTest {

    private BinaryExpressionEntity binaryExpression;
    private ExpressionEntity expr1;
    private ExpressionEntity expr2;

    @BeforeEach
    void setUp() {
        this.expr1 = new PrimitivePrimaryExpressionEntity<>(true, PrimaryExpressionEntityType.BOOLEAN);
        this.expr2 = new PrimitivePrimaryExpressionEntity<>(false, PrimaryExpressionEntityType.BOOLEAN);
        this.binaryExpression = new BinaryExpressionEntity(BinaryOperator.LOGICAL_OR, expr1, expr2);
    }

    @Test
    void getOperator() {
        assertEquals(BinaryOperator.LOGICAL_OR, this.binaryExpression.getOperator());
    }

    @Test
    void getLeft() {
        assertEquals(expr1, this.binaryExpression.getLeft());
    }

    @Test
    void getRight() {
        assertEquals(expr2, this.binaryExpression.getRight());
    }

    @Test
    void toJavaCode() {
        assertEquals("new BinaryExpressionEntity(BinaryOperator.LOGICAL_OR, new PrimitivePrimaryExpressionEntity(true, BOOLEAN), new PrimitivePrimaryExpressionEntity(false, BOOLEAN))", this.binaryExpression.toJavaCode());
    }

    @Test
    void testToString() {
        assertEquals("{{(PrimitiveValue) true} [LOGICAL_OR] {(PrimitiveValue) false}}", this.binaryExpression.toString());
    }
}