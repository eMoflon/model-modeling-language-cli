package de.nexus.expr;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UnaryOperatorTest {
    @Test
    void applyBool() {
        assertAll(
                () -> assertTrue(UnaryOperator.NEGATION.apply(ValueWrapper.create(false)).getAsBoolean()),
                () -> assertFalse(UnaryOperator.NEGATION.apply(ValueWrapper.create(true)).getAsBoolean())
        );
    }
}