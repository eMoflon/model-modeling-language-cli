package de.nexus.expr;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UnaryOperatorTest {
    @Test
    void applyBool() {
        assertAll(
                () -> assertTrue(UnaryOperator.NEGATION.applyBool(false)),
                () -> assertFalse(UnaryOperator.NEGATION.applyBool(true))
        );
    }
}