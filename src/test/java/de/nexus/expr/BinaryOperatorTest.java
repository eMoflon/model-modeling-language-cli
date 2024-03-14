package de.nexus.expr;

import hipe.pattern.ComparatorType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BinaryOperatorTest {

    @Test
    void asHiPEComparatorType() {
        assertAll(
                () -> assertEquals(ComparatorType.EQUAL, BinaryOperator.EQUALS.asHiPEComparatorType()),
                () -> assertEquals(ComparatorType.UNEQUAL, BinaryOperator.NOT_EQUALS.asHiPEComparatorType()),
                () -> assertEquals(ComparatorType.GREATER, BinaryOperator.GREATER_THAN.asHiPEComparatorType()),
                () -> assertEquals(ComparatorType.LESS, BinaryOperator.LESS_THAN.asHiPEComparatorType()),
                () -> assertEquals(ComparatorType.GREATER_OR_EQUAL, BinaryOperator.GREATER_EQUAL_THAN.asHiPEComparatorType()),
                () -> assertEquals(ComparatorType.LESS_OR_EQUAL, BinaryOperator.LESS_EQUAL_THAN.asHiPEComparatorType()),
                () -> assertNull(BinaryOperator.LOGICAL_AND.asHiPEComparatorType()),
                () -> assertNull(BinaryOperator.LOGICAL_OR.asHiPEComparatorType()),
                () -> assertNull(BinaryOperator.ADDITION.asHiPEComparatorType()),
                () -> assertNull(BinaryOperator.SUBTRACTION.asHiPEComparatorType()),
                () -> assertNull(BinaryOperator.MULTIPLICATION.asHiPEComparatorType()),
                () -> assertNull(BinaryOperator.DIVISION.asHiPEComparatorType()),
                () -> assertNull(BinaryOperator.EXPONENTIATION.asHiPEComparatorType()),
                () -> assertNull(BinaryOperator.MODULO.asHiPEComparatorType())
        );
    }

    @Test
    void isBooleanOperator() {
        assertAll(
                () -> assertTrue(BinaryOperator.EQUALS.isBooleanOperator()),
                () -> assertTrue(BinaryOperator.NOT_EQUALS.isBooleanOperator()),
                () -> assertFalse(BinaryOperator.GREATER_THAN.isBooleanOperator()),
                () -> assertFalse(BinaryOperator.LESS_THAN.isBooleanOperator()),
                () -> assertFalse(BinaryOperator.GREATER_EQUAL_THAN.isBooleanOperator()),
                () -> assertFalse(BinaryOperator.LESS_EQUAL_THAN.isBooleanOperator()),
                () -> assertTrue(BinaryOperator.LOGICAL_AND.isBooleanOperator()),
                () -> assertTrue(BinaryOperator.LOGICAL_OR.isBooleanOperator()),
                () -> assertFalse(BinaryOperator.ADDITION.isBooleanOperator()),
                () -> assertFalse(BinaryOperator.SUBTRACTION.isBooleanOperator()),
                () -> assertFalse(BinaryOperator.MULTIPLICATION.isBooleanOperator()),
                () -> assertFalse(BinaryOperator.DIVISION.isBooleanOperator()),
                () -> assertFalse(BinaryOperator.EXPONENTIATION.isBooleanOperator()),
                () -> assertFalse(BinaryOperator.MODULO.isBooleanOperator())
        );
    }

    @Test
    void isHiPEComparator() {
        assertAll(
                () -> assertTrue(BinaryOperator.EQUALS.isHiPEComparator()),
                () -> assertTrue(BinaryOperator.NOT_EQUALS.isHiPEComparator()),
                () -> assertTrue(BinaryOperator.GREATER_THAN.isHiPEComparator()),
                () -> assertTrue(BinaryOperator.LESS_THAN.isHiPEComparator()),
                () -> assertTrue(BinaryOperator.GREATER_EQUAL_THAN.isHiPEComparator()),
                () -> assertTrue(BinaryOperator.LESS_EQUAL_THAN.isHiPEComparator()),
                () -> assertFalse(BinaryOperator.LOGICAL_AND.isHiPEComparator()),
                () -> assertFalse(BinaryOperator.LOGICAL_OR.isHiPEComparator()),
                () -> assertFalse(BinaryOperator.ADDITION.isHiPEComparator()),
                () -> assertFalse(BinaryOperator.SUBTRACTION.isHiPEComparator()),
                () -> assertFalse(BinaryOperator.MULTIPLICATION.isHiPEComparator()),
                () -> assertFalse(BinaryOperator.DIVISION.isHiPEComparator()),
                () -> assertFalse(BinaryOperator.EXPONENTIATION.isHiPEComparator()),
                () -> assertFalse(BinaryOperator.MODULO.isHiPEComparator())
        );
    }

    @Test
    void applyBool() {
        assertAll(
                () -> assertFalse(BinaryOperator.EQUALS.applyBool(1, 2)),
                () -> assertTrue(BinaryOperator.EQUALS.applyBool(1, 1)),
                () -> assertTrue(BinaryOperator.NOT_EQUALS.applyBool(1, 2)),
                () -> assertFalse(BinaryOperator.NOT_EQUALS.applyBool(1, 1)),
                () -> assertFalse(BinaryOperator.GREATER_THAN.applyBool(1, 2)),
                () -> assertFalse(BinaryOperator.GREATER_THAN.applyBool(1, 1)),
                () -> assertTrue(BinaryOperator.GREATER_THAN.applyBool(2, 1)),
                () -> assertTrue(BinaryOperator.LESS_THAN.applyBool(1, 2)),
                () -> assertFalse(BinaryOperator.LESS_THAN.applyBool(1, 1)),
                () -> assertFalse(BinaryOperator.LESS_THAN.applyBool(2, 1)),
                () -> assertFalse(BinaryOperator.GREATER_EQUAL_THAN.applyBool(1, 2)),
                () -> assertTrue(BinaryOperator.GREATER_EQUAL_THAN.applyBool(1, 1)),
                () -> assertTrue(BinaryOperator.GREATER_EQUAL_THAN.applyBool(1, 0)),
                () -> assertTrue(BinaryOperator.LESS_EQUAL_THAN.applyBool(1, 2)),
                () -> assertTrue(BinaryOperator.LESS_EQUAL_THAN.applyBool(1, 1)),
                () -> assertFalse(BinaryOperator.LESS_EQUAL_THAN.applyBool(1, 0)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.LOGICAL_AND.applyBool(1, 2)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.LOGICAL_OR.applyBool(1, 2)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.ADDITION.applyBool(1, 2)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.SUBTRACTION.applyBool(1, 2)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.MULTIPLICATION.applyBool(1, 2)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.DIVISION.applyBool(1, 2)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.EXPONENTIATION.applyBool(1, 2)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.MODULO.applyBool(1, 2))
        );
    }

    @Test
    void testApplyBool() {
        assertAll(
                () -> assertFalse(BinaryOperator.EQUALS.applyBool(1.0, 2.0)),
                () -> assertTrue(BinaryOperator.EQUALS.applyBool(1.0, 1.0)),
                () -> assertTrue(BinaryOperator.NOT_EQUALS.applyBool(1.0, 2.0)),
                () -> assertFalse(BinaryOperator.NOT_EQUALS.applyBool(1.0, 1.0)),
                () -> assertFalse(BinaryOperator.GREATER_THAN.applyBool(1.0, 2.0)),
                () -> assertFalse(BinaryOperator.GREATER_THAN.applyBool(1.0, 1.0)),
                () -> assertTrue(BinaryOperator.GREATER_THAN.applyBool(2.0, 1.0)),
                () -> assertTrue(BinaryOperator.LESS_THAN.applyBool(1.0, 2.0)),
                () -> assertFalse(BinaryOperator.LESS_THAN.applyBool(1.0, 1.0)),
                () -> assertFalse(BinaryOperator.LESS_THAN.applyBool(2.0, 1.0)),
                () -> assertFalse(BinaryOperator.GREATER_EQUAL_THAN.applyBool(1.0, 2.0)),
                () -> assertTrue(BinaryOperator.GREATER_EQUAL_THAN.applyBool(1.0, 1.0)),
                () -> assertTrue(BinaryOperator.GREATER_EQUAL_THAN.applyBool(1.0, 0)),
                () -> assertTrue(BinaryOperator.LESS_EQUAL_THAN.applyBool(1.0, 2.0)),
                () -> assertTrue(BinaryOperator.LESS_EQUAL_THAN.applyBool(1.0, 1.0)),
                () -> assertFalse(BinaryOperator.LESS_EQUAL_THAN.applyBool(1.0, 0.0)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.LOGICAL_AND.applyBool(1.0, 2.0)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.LOGICAL_OR.applyBool(1.0, 2.0)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.ADDITION.applyBool(1.0, 2.0)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.SUBTRACTION.applyBool(1.0, 2.0)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.MULTIPLICATION.applyBool(1.0, 2.0)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.DIVISION.applyBool(1.0, 2.0)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.EXPONENTIATION.applyBool(1.0, 2.0)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.MODULO.applyBool(1.0, 2.0))
        );
    }

    @Test
    void testApplyBool1() {
        assertAll(
                () -> assertFalse(BinaryOperator.EQUALS.applyBool(1, 2.0)),
                () -> assertTrue(BinaryOperator.EQUALS.applyBool(1, 1.0)),
                () -> assertTrue(BinaryOperator.NOT_EQUALS.applyBool(1, 2.0)),
                () -> assertFalse(BinaryOperator.NOT_EQUALS.applyBool(1, 1.0)),
                () -> assertFalse(BinaryOperator.GREATER_THAN.applyBool(1, 2.0)),
                () -> assertFalse(BinaryOperator.GREATER_THAN.applyBool(1, 1.0)),
                () -> assertTrue(BinaryOperator.GREATER_THAN.applyBool(2, 1.0)),
                () -> assertTrue(BinaryOperator.LESS_THAN.applyBool(1, 2.0)),
                () -> assertFalse(BinaryOperator.LESS_THAN.applyBool(1, 1.0)),
                () -> assertFalse(BinaryOperator.LESS_THAN.applyBool(2, 1.0)),
                () -> assertFalse(BinaryOperator.GREATER_EQUAL_THAN.applyBool(1, 2.0)),
                () -> assertTrue(BinaryOperator.GREATER_EQUAL_THAN.applyBool(1, 1.0)),
                () -> assertTrue(BinaryOperator.GREATER_EQUAL_THAN.applyBool(1, 0)),
                () -> assertTrue(BinaryOperator.LESS_EQUAL_THAN.applyBool(1, 2.0)),
                () -> assertTrue(BinaryOperator.LESS_EQUAL_THAN.applyBool(1, 1.0)),
                () -> assertFalse(BinaryOperator.LESS_EQUAL_THAN.applyBool(1, 0.0)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.LOGICAL_AND.applyBool(1, 2.0)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.LOGICAL_OR.applyBool(1, 2.0)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.ADDITION.applyBool(1, 2.0)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.SUBTRACTION.applyBool(1, 2.0)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.MULTIPLICATION.applyBool(1, 2.0)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.DIVISION.applyBool(1, 2.0)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.EXPONENTIATION.applyBool(1, 2.0)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.MODULO.applyBool(1, 2.0))
        );
    }

    @Test
    void testApplyBool2() {
        assertAll(
                () -> assertFalse(BinaryOperator.EQUALS.applyBool(1.0, 2)),
                () -> assertTrue(BinaryOperator.EQUALS.applyBool(1.0, 1)),
                () -> assertTrue(BinaryOperator.NOT_EQUALS.applyBool(1.0, 2)),
                () -> assertFalse(BinaryOperator.NOT_EQUALS.applyBool(1.0, 1)),
                () -> assertFalse(BinaryOperator.GREATER_THAN.applyBool(1.0, 2)),
                () -> assertFalse(BinaryOperator.GREATER_THAN.applyBool(1.0, 1)),
                () -> assertTrue(BinaryOperator.GREATER_THAN.applyBool(2.0, 1)),
                () -> assertTrue(BinaryOperator.LESS_THAN.applyBool(1.0, 2)),
                () -> assertFalse(BinaryOperator.LESS_THAN.applyBool(1.0, 1)),
                () -> assertFalse(BinaryOperator.LESS_THAN.applyBool(2.0, 1)),
                () -> assertFalse(BinaryOperator.GREATER_EQUAL_THAN.applyBool(1.0, 2)),
                () -> assertTrue(BinaryOperator.GREATER_EQUAL_THAN.applyBool(1.0, 1)),
                () -> assertTrue(BinaryOperator.GREATER_EQUAL_THAN.applyBool(1.0, 0)),
                () -> assertTrue(BinaryOperator.LESS_EQUAL_THAN.applyBool(1.0, 2)),
                () -> assertTrue(BinaryOperator.LESS_EQUAL_THAN.applyBool(1.0, 1)),
                () -> assertFalse(BinaryOperator.LESS_EQUAL_THAN.applyBool(1.0, 0)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.LOGICAL_AND.applyBool(1.0, 2)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.LOGICAL_OR.applyBool(1.0, 2)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.ADDITION.applyBool(1.0, 2)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.SUBTRACTION.applyBool(1.0, 2)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.MULTIPLICATION.applyBool(1.0, 2)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.DIVISION.applyBool(1.0, 2)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.EXPONENTIATION.applyBool(1.0, 2)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.MODULO.applyBool(1.0, 2))
        );
    }

    @Test
    void testApplyBool3() {
        assertAll(
                () -> assertTrue(BinaryOperator.EQUALS.applyBool(false, false)),
                () -> assertFalse(BinaryOperator.EQUALS.applyBool(false, true)),
                () -> assertFalse(BinaryOperator.EQUALS.applyBool(true, false)),
                () -> assertTrue(BinaryOperator.EQUALS.applyBool(true, true)),
                () -> assertFalse(BinaryOperator.NOT_EQUALS.applyBool(false, false)),
                () -> assertTrue(BinaryOperator.NOT_EQUALS.applyBool(false, true)),
                () -> assertTrue(BinaryOperator.NOT_EQUALS.applyBool(true, false)),
                () -> assertFalse(BinaryOperator.NOT_EQUALS.applyBool(true, true)),
                () -> assertFalse(BinaryOperator.LOGICAL_AND.applyBool(false, false)),
                () -> assertFalse(BinaryOperator.LOGICAL_AND.applyBool(false, true)),
                () -> assertFalse(BinaryOperator.LOGICAL_AND.applyBool(true, false)),
                () -> assertTrue(BinaryOperator.LOGICAL_AND.applyBool(true, true)),
                () -> assertFalse(BinaryOperator.LOGICAL_OR.applyBool(false, false)),
                () -> assertTrue(BinaryOperator.LOGICAL_OR.applyBool(false, true)),
                () -> assertTrue(BinaryOperator.LOGICAL_OR.applyBool(true, false)),
                () -> assertTrue(BinaryOperator.LOGICAL_OR.applyBool(true, true)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.GREATER_THAN.applyBool(true, false)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.LESS_THAN.applyBool(true, false)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.GREATER_EQUAL_THAN.applyBool(true, false)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.LESS_EQUAL_THAN.applyBool(true, false)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.ADDITION.applyBool(true, false)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.SUBTRACTION.applyBool(true, false)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.MULTIPLICATION.applyBool(true, false)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.DIVISION.applyBool(true, false)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.EXPONENTIATION.applyBool(true, false)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.MODULO.applyBool(true, false))
        );
    }

    @Test
    void testApplyBool4() {
        assertAll(
                () -> assertTrue(BinaryOperator.EQUALS.applyBool("ValA", "ValA")),
                () -> assertFalse(BinaryOperator.EQUALS.applyBool("ValA", "ValB")),
                () -> assertFalse(BinaryOperator.NOT_EQUALS.applyBool("ValA", "ValA")),
                () -> assertTrue(BinaryOperator.NOT_EQUALS.applyBool("ValA", "ValB")),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.GREATER_THAN.applyBool("ValA", "ValB")),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.LESS_THAN.applyBool("ValA", "ValB")),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.GREATER_EQUAL_THAN.applyBool("ValA", "ValB")),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.LESS_EQUAL_THAN.applyBool("ValA", "ValB")),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.LOGICAL_AND.applyBool("ValA", "ValB")),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.LOGICAL_OR.applyBool("ValA", "ValB")),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.ADDITION.applyBool("ValA", "ValB")),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.SUBTRACTION.applyBool("ValA", "ValB")),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.MULTIPLICATION.applyBool("ValA", "ValB")),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.DIVISION.applyBool("ValA", "ValB")),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.EXPONENTIATION.applyBool("ValA", "ValB")),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.MODULO.applyBool("ValA", "ValB"))
        );
    }

    @Test
    void applyString() {
        assertAll(
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.EQUALS.applyString(1, 2)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.NOT_EQUALS.applyString(1, 2)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.GREATER_THAN.applyString(1, 2)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.LESS_THAN.applyString(1, 2)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.GREATER_EQUAL_THAN.applyString(1, 2)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.LESS_EQUAL_THAN.applyString(1, 2)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.LOGICAL_AND.applyString(1, 2)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.LOGICAL_OR.applyString(1, 2)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.ADDITION.applyString(1, 2)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.SUBTRACTION.applyString(1, 2)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.MULTIPLICATION.applyString(1, 2)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.DIVISION.applyString(1, 2)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.EXPONENTIATION.applyString(1, 2)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.MODULO.applyString(1, 2))
        );
    }

    @Test
    void testApplyString() {
        assertAll(
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.EQUALS.applyString(1.0, 2.0)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.NOT_EQUALS.applyString(1.0, 2.0)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.GREATER_THAN.applyString(1.0, 2.0)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.LESS_THAN.applyString(1.0, 2.0)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.GREATER_EQUAL_THAN.applyString(1.0, 2.0)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.LESS_EQUAL_THAN.applyString(1.0, 2.0)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.LOGICAL_AND.applyString(1.0, 2.0)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.LOGICAL_OR.applyString(1.0, 2.0)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.ADDITION.applyString(1.0, 2.0)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.SUBTRACTION.applyString(1.0, 2.0)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.MULTIPLICATION.applyString(1.0, 2.0)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.DIVISION.applyString(1.0, 2.0)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.EXPONENTIATION.applyString(1.0, 2.0)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.MODULO.applyString(1.0, 2.0))
        );
    }

    @Test
    void testApplyString1() {
        assertAll(
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.EQUALS.applyString(1, 2.0)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.NOT_EQUALS.applyString(1, 2.0)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.GREATER_THAN.applyString(1, 2.0)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.LESS_THAN.applyString(1, 2.0)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.GREATER_EQUAL_THAN.applyString(1, 2.0)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.LESS_EQUAL_THAN.applyString(1, 2.0)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.LOGICAL_AND.applyString(1, 2.0)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.LOGICAL_OR.applyString(1, 2.0)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.ADDITION.applyString(1, 2.0)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.SUBTRACTION.applyString(1, 2.0)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.MULTIPLICATION.applyString(1, 2.0)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.DIVISION.applyString(1, 2.0)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.EXPONENTIATION.applyString(1, 2.0)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.MODULO.applyString(1, 2.0))
        );
    }

    @Test
    void testApplyString2() {
        assertAll(
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.EQUALS.applyString(1.0, 2)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.NOT_EQUALS.applyString(1.0, 2)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.GREATER_THAN.applyString(1.0, 2)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.LESS_THAN.applyString(1.0, 2)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.GREATER_EQUAL_THAN.applyString(1.0, 2)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.LESS_EQUAL_THAN.applyString(1.0, 2)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.LOGICAL_AND.applyString(1.0, 2)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.LOGICAL_OR.applyString(1.0, 2)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.ADDITION.applyString(1.0, 2)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.SUBTRACTION.applyString(1.0, 2)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.MULTIPLICATION.applyString(1.0, 2)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.DIVISION.applyString(1.0, 2)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.EXPONENTIATION.applyString(1.0, 2)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.MODULO.applyString(1.0, 2))
        );
    }

    @Test
    void testApplyString3() {
        assertAll(
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.EQUALS.applyString(true, false)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.NOT_EQUALS.applyString(true, false)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.GREATER_THAN.applyString(true, false)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.LESS_THAN.applyString(true, false)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.GREATER_EQUAL_THAN.applyString(true, false)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.LESS_EQUAL_THAN.applyString(true, false)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.LOGICAL_AND.applyString(true, false)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.LOGICAL_OR.applyString(true, false)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.ADDITION.applyString(true, false)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.SUBTRACTION.applyString(true, false)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.MULTIPLICATION.applyString(true, false)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.DIVISION.applyString(true, false)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.EXPONENTIATION.applyString(true, false)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.MODULO.applyString(true, false))
        );
    }

    @Test
    void testApplyString4() {
        assertAll(
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.EQUALS.applyString(2, "test")),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.NOT_EQUALS.applyString(2, "test")),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.GREATER_THAN.applyString(2, "test")),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.LESS_THAN.applyString(2, "test")),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.GREATER_EQUAL_THAN.applyString(2, "test")),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.LESS_EQUAL_THAN.applyString(2, "test")),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.LOGICAL_AND.applyString(2, "test")),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.LOGICAL_OR.applyString(2, "test")),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.ADDITION.applyString(2, "test")),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.SUBTRACTION.applyString(2, "test")),
                () -> assertEquals("testtest", BinaryOperator.MULTIPLICATION.applyString(2, "test")),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.DIVISION.applyString(2, "test")),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.EXPONENTIATION.applyString(2, "test")),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.MODULO.applyString(2, "test"))
        );
    }

    @Test
    void testApplyString5() {
        assertAll(
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.EQUALS.applyString("test", 2)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.NOT_EQUALS.applyString("test", 2)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.GREATER_THAN.applyString("test", 2)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.LESS_THAN.applyString("test", 2)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.GREATER_EQUAL_THAN.applyString("test", 2)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.LESS_EQUAL_THAN.applyString("test", 2)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.LOGICAL_AND.applyString("test", 2)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.LOGICAL_OR.applyString("test", 2)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.ADDITION.applyString("test", 2)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.SUBTRACTION.applyString("test", 2)),
                () -> assertEquals("testtest", BinaryOperator.MULTIPLICATION.applyString("test", 2)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.DIVISION.applyString("test", 2)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.EXPONENTIATION.applyString("test", 2)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.MODULO.applyString("test", 2))
        );
    }

    @Test
    void testApplyString6() {
        assertAll(
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.EQUALS.applyString("test", "value")),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.NOT_EQUALS.applyString("test", "value")),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.GREATER_THAN.applyString("test", "value")),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.LESS_THAN.applyString("test", "value")),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.GREATER_EQUAL_THAN.applyString("test", "value")),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.LESS_EQUAL_THAN.applyString("test", "value")),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.LOGICAL_AND.applyString("test", "value")),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.LOGICAL_OR.applyString("test", "value")),
                () -> assertEquals("testvalue", BinaryOperator.ADDITION.applyString("test", "value")),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.SUBTRACTION.applyString("test", "value")),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.MULTIPLICATION.applyString("test", "value")),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.DIVISION.applyString("test", "value")),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.EXPONENTIATION.applyString("test", "value")),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.MODULO.applyString("test", "value"))
        );
    }

    @Test
    void applyDouble() {
        assertAll(
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.EQUALS.applyDouble(1, 2)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.NOT_EQUALS.applyDouble(1, 2)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.GREATER_THAN.applyDouble(1, 2)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.LESS_THAN.applyDouble(1, 2)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.GREATER_EQUAL_THAN.applyDouble(1, 2)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.LESS_EQUAL_THAN.applyDouble(1, 2)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.LOGICAL_AND.applyDouble(1, 2)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.LOGICAL_OR.applyDouble(1, 2)),
                () -> assertEquals(3.0, BinaryOperator.ADDITION.applyDouble(1, 2)),
                () -> assertEquals(-1.0, BinaryOperator.SUBTRACTION.applyDouble(1, 2)),
                () -> assertEquals(2.0, BinaryOperator.MULTIPLICATION.applyDouble(1, 2)),
                () -> assertEquals(0.5, BinaryOperator.DIVISION.applyDouble(1, 2)),
                () -> assertEquals(1.0, BinaryOperator.EXPONENTIATION.applyDouble(1, 2)),
                () -> assertEquals(1.0, BinaryOperator.MODULO.applyDouble(1, 2))
        );
    }

    @Test
    void testApplyDouble() {
        assertAll(
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.EQUALS.applyDouble(1.0, 2.0)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.NOT_EQUALS.applyDouble(1.0, 2.0)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.GREATER_THAN.applyDouble(1.0, 2.0)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.LESS_THAN.applyDouble(1.0, 2.0)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.GREATER_EQUAL_THAN.applyDouble(1.0, 2.0)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.LESS_EQUAL_THAN.applyDouble(1.0, 2.0)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.LOGICAL_AND.applyDouble(1.0, 2.0)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.LOGICAL_OR.applyDouble(1.0, 2.0)),
                () -> assertEquals(3.0, BinaryOperator.ADDITION.applyDouble(1.0, 2.0)),
                () -> assertEquals(-1.0, BinaryOperator.SUBTRACTION.applyDouble(1.0, 2.0)),
                () -> assertEquals(2.0, BinaryOperator.MULTIPLICATION.applyDouble(1.0, 2.0)),
                () -> assertEquals(0.5, BinaryOperator.DIVISION.applyDouble(1.0, 2.0)),
                () -> assertEquals(1.0, BinaryOperator.EXPONENTIATION.applyDouble(1.0, 2.0)),
                () -> assertEquals(1.0, BinaryOperator.MODULO.applyDouble(1.0, 2.0))
        );
    }

    @Test
    void testApplyDouble1() {
        assertAll(
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.EQUALS.applyDouble(1, 2.0)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.NOT_EQUALS.applyDouble(1, 2.0)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.GREATER_THAN.applyDouble(1, 2.0)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.LESS_THAN.applyDouble(1, 2.0)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.GREATER_EQUAL_THAN.applyDouble(1, 2.0)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.LESS_EQUAL_THAN.applyDouble(1, 2.0)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.LOGICAL_AND.applyDouble(1, 2.0)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.LOGICAL_OR.applyDouble(1, 2.0)),
                () -> assertEquals(3.0, BinaryOperator.ADDITION.applyDouble(1, 2.0)),
                () -> assertEquals(-1.0, BinaryOperator.SUBTRACTION.applyDouble(1, 2.0)),
                () -> assertEquals(2.0, BinaryOperator.MULTIPLICATION.applyDouble(1, 2.0)),
                () -> assertEquals(0.5, BinaryOperator.DIVISION.applyDouble(1, 2.0)),
                () -> assertEquals(1.0, BinaryOperator.EXPONENTIATION.applyDouble(1, 2.0)),
                () -> assertEquals(1.0, BinaryOperator.MODULO.applyDouble(1, 2.0))
        );
    }

    @Test
    void testApplyDouble2() {
        assertAll(
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.EQUALS.applyDouble(1.0, 2)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.NOT_EQUALS.applyDouble(1.0, 2)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.GREATER_THAN.applyDouble(1.0, 2)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.LESS_THAN.applyDouble(1.0, 2)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.GREATER_EQUAL_THAN.applyDouble(1.0, 2)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.LESS_EQUAL_THAN.applyDouble(1.0, 2)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.LOGICAL_AND.applyDouble(1.0, 2)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.LOGICAL_OR.applyDouble(1.0, 2)),
                () -> assertEquals(3.0, BinaryOperator.ADDITION.applyDouble(1.0, 2)),
                () -> assertEquals(-1.0, BinaryOperator.SUBTRACTION.applyDouble(1.0, 2)),
                () -> assertEquals(2.0, BinaryOperator.MULTIPLICATION.applyDouble(1.0, 2)),
                () -> assertEquals(0.5, BinaryOperator.DIVISION.applyDouble(1.0, 2)),
                () -> assertEquals(1.0, BinaryOperator.EXPONENTIATION.applyDouble(1.0, 2)),
                () -> assertEquals(1.0, BinaryOperator.MODULO.applyDouble(1.0, 2))
        );
    }

    @Test
    void testApplyDouble3() {
        assertAll(
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.EQUALS.applyDouble(true, false)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.NOT_EQUALS.applyDouble(true, false)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.GREATER_THAN.applyDouble(true, false)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.LESS_THAN.applyDouble(true, false)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.GREATER_EQUAL_THAN.applyDouble(true, false)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.LESS_EQUAL_THAN.applyDouble(true, false)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.LOGICAL_AND.applyDouble(true, false)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.LOGICAL_OR.applyDouble(true, false)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.ADDITION.applyDouble(true, false)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.SUBTRACTION.applyDouble(true, false)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.MULTIPLICATION.applyDouble(true, false)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.DIVISION.applyDouble(true, false)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.EXPONENTIATION.applyDouble(true, false)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.MODULO.applyDouble(true, false))
        );
    }

    @Test
    void testApplyDouble4() {
        assertAll(
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.EQUALS.applyDouble("test", "value")),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.NOT_EQUALS.applyDouble("test", "value")),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.GREATER_THAN.applyDouble("test", "value")),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.LESS_THAN.applyDouble("test", "value")),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.GREATER_EQUAL_THAN.applyDouble("test", "value")),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.LESS_EQUAL_THAN.applyDouble("test", "value")),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.LOGICAL_AND.applyDouble("test", "value")),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.LOGICAL_OR.applyDouble("test", "value")),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.ADDITION.applyDouble("test", "value")),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.SUBTRACTION.applyDouble("test", "value")),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.MULTIPLICATION.applyDouble("test", "value")),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.DIVISION.applyDouble("test", "value")),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.EXPONENTIATION.applyDouble("test", "value")),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.MODULO.applyDouble("test", "value"))
        );
    }
}