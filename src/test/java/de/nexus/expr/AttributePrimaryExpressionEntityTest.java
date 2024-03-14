package de.nexus.expr;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AttributePrimaryExpressionEntityTest {
    AttributePrimaryExpressionEntity attributePrimaryExpr;

    @BeforeEach
    void setUp() {
        attributePrimaryExpr = new AttributePrimaryExpressionEntity("TestClass", "TargetAttr", "test.node.id");
    }

    @Test
    void getType() {
        assertEquals(PrimaryExpressionEntityType.ATTRIBUTE, this.attributePrimaryExpr.getType());
    }

    @Test
    void getNodeId() {
        assertEquals("test.node.id", this.attributePrimaryExpr.getNodeId());
    }

    @Test
    void toJavaCode() {
        Throwable exception = assertThrows(UnsupportedOperationException.class, this.attributePrimaryExpr::toJavaCode);
        assertEquals("AttributePrimaryExpressions cannot be transfered to the model server!", exception.getMessage());
    }

    @Test
    void testToString() {
        assertEquals("{(AttributeValue) TestClass -> TargetAttr}", this.attributePrimaryExpr.toString());
    }

    @Test
    void getClassName() {
        assertEquals("TestClass", this.attributePrimaryExpr.getClassName());
    }

    @Test
    void getElementName() {
        assertEquals("TargetAttr", this.attributePrimaryExpr.getElementName());
    }
}