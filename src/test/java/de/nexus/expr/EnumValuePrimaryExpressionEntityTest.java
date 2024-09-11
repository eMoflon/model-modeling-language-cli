package de.nexus.expr;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EnumValuePrimaryExpressionEntityTest {

    EnumValuePrimaryExpressionEntity enumValueExpr;

    @BeforeEach
    void setUp() {
        enumValueExpr = new EnumValuePrimaryExpressionEntity("TestEEnum", "TestVal");
    }

    @Test
    void getType() {
        assertEquals(PrimaryExpressionEntityType.ENUM_VALUE, this.enumValueExpr.getType());
    }

    @Test
    void getEnumName() {
        assertEquals("TestEEnum", this.enumValueExpr.getEnumName());
    }

    @Test
    void getValue() {
        assertEquals("TestVal", this.enumValueExpr.getValue());
    }

    @Test
    void toJavaCode() {
        Throwable exception = assertThrows(UnsupportedOperationException.class, this.enumValueExpr::toJavaCode);
        assertEquals("EnumValuePrimaryExpressions cannot be transfered to the model server!", exception.getMessage());
    }

    @Test
    void testToString() {
        assertEquals("{(EnumValue | TestEEnum) TestVal}", this.enumValueExpr.toString());
    }
}