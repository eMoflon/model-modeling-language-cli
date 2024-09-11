package de.nexus.expr;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PatternPrimaryExpressionEntityTest {

    PatternPrimaryExpressionEntity patternPrimaryExpr;

    @BeforeEach
    void setUp() {
        patternPrimaryExpr = new PatternPrimaryExpressionEntity("test.node.id", "Test Pattern");
    }

    @Test
    void getType() {
        assertEquals(PrimaryExpressionEntityType.PATTERN_INVOCATION, this.patternPrimaryExpr.getType());
    }

    @Test
    void getNodeId() {
        assertEquals("test.node.id", this.patternPrimaryExpr.getNodeId());
    }

    @Test
    void getPatternName() {
        assertEquals("Test Pattern", this.patternPrimaryExpr.getPatternName());
    }

    @Test
    void toJavaCode() {
        assertEquals("new PatternPrimaryExpressionEntity(\"test.node.id\", \"Test Pattern\")", this.patternPrimaryExpr.toJavaCode());
    }

    @Test
    void testToString() {
        assertEquals("{(PatternExistanceValue) Test Pattern}", this.patternPrimaryExpr.toString());
    }
}