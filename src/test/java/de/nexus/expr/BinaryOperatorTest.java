package de.nexus.expr;

import hipe.pattern.ComparatorType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BinaryOperatorTest {
    private ValueWrapper<Integer> int0;
    private ValueWrapper<Integer> int1;
    private ValueWrapper<Integer> int2;
    private ValueWrapper<Double> double0;
    private ValueWrapper<Double> double1;
    private ValueWrapper<Double> double2;
    private ValueWrapper<Boolean> boolTrue;
    private ValueWrapper<Boolean> boolFalse;
    private ValueWrapper<String> stringA;
    private ValueWrapper<String> stringB;

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

    @BeforeEach
    void setUp() {
        this.int0 = ValueWrapper.create(0);
        this.int1 = ValueWrapper.create(1);
        this.int2 = ValueWrapper.create(2);
        this.double0 = ValueWrapper.create(0.0);
        this.double1 = ValueWrapper.create(1.0);
        this.double2 = ValueWrapper.create(2.0);
        this.boolTrue = ValueWrapper.create(true);
        this.boolFalse = ValueWrapper.create(false);
        this.stringA = ValueWrapper.create("TestValA");
        this.stringB = ValueWrapper.create("TestValB");
    }

    private void assertValTrue(ValueWrapper<?> value) {
        Assertions.assertTrue(value.isBoolean());
        Assertions.assertTrue(value.getAsBoolean());
    }

    private void assertValFalse(ValueWrapper<?> value) {
        Assertions.assertTrue(value.isBoolean());
        Assertions.assertFalse(value.getAsBoolean());
    }

    private void assertStringEq(String expected, ValueWrapper<?> value) {
        Assertions.assertTrue(value.isString());
        Assertions.assertEquals(expected, value.getAsString());
    }

    private void assertIntEq(int expected, ValueWrapper<?> value) {
        Assertions.assertTrue(value.isInteger());
        Assertions.assertEquals(expected, value.getAsInt());
    }

    private void assertDoubleEq(double expected, ValueWrapper<?> value) {
        Assertions.assertTrue(value.isDouble());
        Assertions.assertEquals(expected, value.getAsDouble());
    }

    @Test
    void operatorEquals() {
        assertAll(
                () -> assertValTrue(BinaryOperator.EQUALS.apply(int0, int0)),
                () -> assertValFalse(BinaryOperator.EQUALS.apply(int0, int1)),
                () -> assertValTrue(BinaryOperator.EQUALS.apply(double0, double0)),
                () -> assertValFalse(BinaryOperator.EQUALS.apply(double0, double1)),
                () -> assertValTrue(BinaryOperator.EQUALS.apply(boolTrue, boolTrue)),
                () -> assertValFalse(BinaryOperator.EQUALS.apply(boolTrue, boolFalse)),
                () -> assertValTrue(BinaryOperator.EQUALS.apply(stringA, stringA)),
                () -> assertValFalse(BinaryOperator.EQUALS.apply(stringA, stringB)),
                () -> assertValFalse(BinaryOperator.EQUALS.apply(int0, double0)),
                () -> assertValFalse(BinaryOperator.EQUALS.apply(int0, boolTrue)),
                () -> assertValFalse(BinaryOperator.EQUALS.apply(int0, stringA)),
                () -> assertValFalse(BinaryOperator.EQUALS.apply(double0, boolTrue)),
                () -> assertValFalse(BinaryOperator.EQUALS.apply(double0, stringA)),
                () -> assertValFalse(BinaryOperator.EQUALS.apply(boolTrue, stringA))
        );
    }

    @Test
    void operatorNotEquals() {
        assertAll(
                () -> assertValFalse(BinaryOperator.NOT_EQUALS.apply(int0, int0)),
                () -> assertValTrue(BinaryOperator.NOT_EQUALS.apply(int0, int1)),
                () -> assertValFalse(BinaryOperator.NOT_EQUALS.apply(double0, double0)),
                () -> assertValTrue(BinaryOperator.NOT_EQUALS.apply(double0, double1)),
                () -> assertValFalse(BinaryOperator.NOT_EQUALS.apply(boolTrue, boolTrue)),
                () -> assertValTrue(BinaryOperator.NOT_EQUALS.apply(boolTrue, boolFalse)),
                () -> assertValFalse(BinaryOperator.NOT_EQUALS.apply(stringA, stringA)),
                () -> assertValTrue(BinaryOperator.NOT_EQUALS.apply(stringA, stringB)),
                () -> assertValTrue(BinaryOperator.NOT_EQUALS.apply(int0, double0)),
                () -> assertValTrue(BinaryOperator.NOT_EQUALS.apply(int0, boolTrue)),
                () -> assertValTrue(BinaryOperator.NOT_EQUALS.apply(int0, stringA)),
                () -> assertValTrue(BinaryOperator.NOT_EQUALS.apply(double0, boolTrue)),
                () -> assertValTrue(BinaryOperator.NOT_EQUALS.apply(double0, stringA)),
                () -> assertValTrue(BinaryOperator.NOT_EQUALS.apply(boolTrue, stringA))
        );
    }

    @Test
    void operatorGreater() {
        assertAll(
                () -> assertValTrue(BinaryOperator.GREATER_THAN.apply(int1, int0)),
                () -> assertValFalse(BinaryOperator.GREATER_THAN.apply(int1, int1)),
                () -> assertValFalse(BinaryOperator.GREATER_THAN.apply(int1, int2)),
                () -> assertValTrue(BinaryOperator.GREATER_THAN.apply(double1, double0)),
                () -> assertValFalse(BinaryOperator.GREATER_THAN.apply(double1, double1)),
                () -> assertValFalse(BinaryOperator.GREATER_THAN.apply(double1, double2)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.GREATER_THAN.apply(boolTrue, boolFalse)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.GREATER_THAN.apply(stringA, stringB)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.GREATER_THAN.apply(boolTrue, stringA)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.GREATER_THAN.apply(int0, boolFalse)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.GREATER_THAN.apply(double0, boolFalse))
        );
    }

    @Test
    void operatorGreaterEqual() {
        assertAll(
                () -> assertValTrue(BinaryOperator.GREATER_EQUAL_THAN.apply(int1, int0)),
                () -> assertValTrue(BinaryOperator.GREATER_EQUAL_THAN.apply(int1, int1)),
                () -> assertValFalse(BinaryOperator.GREATER_EQUAL_THAN.apply(int1, int2)),
                () -> assertValTrue(BinaryOperator.GREATER_EQUAL_THAN.apply(double1, double0)),
                () -> assertValTrue(BinaryOperator.GREATER_EQUAL_THAN.apply(double1, double1)),
                () -> assertValFalse(BinaryOperator.GREATER_EQUAL_THAN.apply(double1, double2)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.GREATER_EQUAL_THAN.apply(boolTrue, boolFalse)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.GREATER_EQUAL_THAN.apply(stringA, stringB)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.GREATER_EQUAL_THAN.apply(boolTrue, stringA)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.GREATER_EQUAL_THAN.apply(int0, boolFalse)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.GREATER_EQUAL_THAN.apply(double0, boolFalse))
        );
    }

    @Test
    void operatorLess() {
        assertAll(
                () -> assertValTrue(BinaryOperator.LESS_THAN.apply(int0, int1)),
                () -> assertValFalse(BinaryOperator.LESS_THAN.apply(int1, int1)),
                () -> assertValFalse(BinaryOperator.LESS_THAN.apply(int2, int1)),
                () -> assertValTrue(BinaryOperator.LESS_THAN.apply(double0, double1)),
                () -> assertValFalse(BinaryOperator.LESS_THAN.apply(double1, double1)),
                () -> assertValFalse(BinaryOperator.LESS_THAN.apply(double2, double1)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.LESS_THAN.apply(boolTrue, boolFalse)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.LESS_THAN.apply(stringA, stringB)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.LESS_THAN.apply(boolTrue, stringA)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.LESS_THAN.apply(int0, boolFalse)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.LESS_THAN.apply(double0, boolFalse))
        );
    }

    @Test
    void operatorLessEqual() {
        assertAll(
                () -> assertValTrue(BinaryOperator.LESS_EQUAL_THAN.apply(int0, int1)),
                () -> assertValTrue(BinaryOperator.LESS_EQUAL_THAN.apply(int1, int1)),
                () -> assertValFalse(BinaryOperator.LESS_EQUAL_THAN.apply(int2, int1)),
                () -> assertValTrue(BinaryOperator.LESS_EQUAL_THAN.apply(double0, double1)),
                () -> assertValTrue(BinaryOperator.LESS_EQUAL_THAN.apply(double1, double1)),
                () -> assertValFalse(BinaryOperator.LESS_EQUAL_THAN.apply(double2, double1)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.LESS_EQUAL_THAN.apply(boolTrue, boolFalse)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.LESS_EQUAL_THAN.apply(stringA, stringB)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.LESS_EQUAL_THAN.apply(boolTrue, stringA)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.LESS_EQUAL_THAN.apply(int0, boolFalse)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.LESS_EQUAL_THAN.apply(double0, boolFalse))
        );
    }

    @Test
    void operatorLogicalAnd() {
        assertAll(
                () -> assertValTrue(BinaryOperator.LOGICAL_AND.apply(boolTrue, boolTrue)),
                () -> assertValFalse(BinaryOperator.LOGICAL_AND.apply(boolTrue, boolFalse)),
                () -> assertValFalse(BinaryOperator.LOGICAL_AND.apply(boolFalse, boolTrue)),
                () -> assertValFalse(BinaryOperator.LOGICAL_AND.apply(boolFalse, boolFalse)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.LOGICAL_AND.apply(int0, int1)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.LOGICAL_AND.apply(double0, double1)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.LOGICAL_AND.apply(stringA, stringB)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.LOGICAL_AND.apply(boolTrue, stringA)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.LOGICAL_AND.apply(int0, boolFalse)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.LOGICAL_AND.apply(double0, boolFalse))
        );
    }

    @Test
    void operatorLogicalOr() {
        assertAll(
                () -> assertValTrue(BinaryOperator.LOGICAL_OR.apply(boolTrue, boolTrue)),
                () -> assertValTrue(BinaryOperator.LOGICAL_OR.apply(boolTrue, boolFalse)),
                () -> assertValTrue(BinaryOperator.LOGICAL_OR.apply(boolFalse, boolTrue)),
                () -> assertValFalse(BinaryOperator.LOGICAL_OR.apply(boolFalse, boolFalse)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.LOGICAL_OR.apply(int0, int1)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.LOGICAL_OR.apply(double0, double1)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.LOGICAL_OR.apply(stringA, stringB)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.LOGICAL_OR.apply(boolTrue, stringA)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.LOGICAL_OR.apply(int0, boolFalse)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.LOGICAL_OR.apply(double0, boolFalse))
        );
    }

    @Test
    void operatorAddition() {
        assertAll(
                () -> assertIntEq(3, BinaryOperator.ADDITION.apply(int1, int2)),
                () -> assertDoubleEq(3.0, BinaryOperator.ADDITION.apply(int1, double2)),
                () -> assertDoubleEq(3.0, BinaryOperator.ADDITION.apply(double1, int2)),
                () -> assertDoubleEq(3.0, BinaryOperator.ADDITION.apply(double1, double2)),
                () -> assertStringEq("TestValATestValB", BinaryOperator.ADDITION.apply(stringA, stringB)),
                () -> assertStringEq("TestValA0", BinaryOperator.ADDITION.apply(stringA, int0)),
                () -> assertStringEq("TestValAtrue", BinaryOperator.ADDITION.apply(stringA, boolTrue)),
                () -> assertStringEq("0TestValA", BinaryOperator.ADDITION.apply(int0, stringA)),
                () -> assertStringEq("falseTestValA", BinaryOperator.ADDITION.apply(boolFalse, stringA)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.ADDITION.apply(boolTrue, boolFalse))
        );
    }

    @Test
    void operatorSubtraction() {
        assertAll(
                () -> assertIntEq(-1, BinaryOperator.SUBTRACTION.apply(int1, int2)),
                () -> assertDoubleEq(-1.0, BinaryOperator.SUBTRACTION.apply(int1, double2)),
                () -> assertDoubleEq(-1.0, BinaryOperator.SUBTRACTION.apply(double1, int2)),
                () -> assertDoubleEq(-1.0, BinaryOperator.SUBTRACTION.apply(double1, double2)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.SUBTRACTION.apply(stringA, stringB)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.SUBTRACTION.apply(stringA, int0)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.SUBTRACTION.apply(stringA, boolTrue)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.SUBTRACTION.apply(int0, stringA)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.SUBTRACTION.apply(boolFalse, stringA)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.SUBTRACTION.apply(boolTrue, boolFalse))
        );
    }

    @Test
    void operatorMultiplication() {
        assertAll(
                () -> assertIntEq(4, BinaryOperator.MULTIPLICATION.apply(int2, int2)),
                () -> assertDoubleEq(4.0, BinaryOperator.MULTIPLICATION.apply(int2, double2)),
                () -> assertDoubleEq(4.0, BinaryOperator.MULTIPLICATION.apply(double2, int2)),
                () -> assertDoubleEq(4.0, BinaryOperator.MULTIPLICATION.apply(double2, double2)),
                () -> assertStringEq("TestValATestValA", BinaryOperator.MULTIPLICATION.apply(stringA, int2)),
                () -> assertStringEq("TestValATestValA", BinaryOperator.MULTIPLICATION.apply(int2, stringA)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.MULTIPLICATION.apply(stringA, stringB)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.MULTIPLICATION.apply(stringA, boolTrue)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.MULTIPLICATION.apply(boolTrue, boolFalse)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.MULTIPLICATION.apply(boolFalse, stringA))
        );
    }

    @Test
    void operatorDivision() {
        assertAll(
                () -> assertIntEq(2, BinaryOperator.DIVISION.apply(int2, int1)),
                () -> assertIntEq(0, BinaryOperator.DIVISION.apply(int1, int2)),
                () -> assertDoubleEq(0.5, BinaryOperator.DIVISION.apply(int1, double2)),
                () -> assertDoubleEq(0.5, BinaryOperator.DIVISION.apply(double1, int2)),
                () -> assertDoubleEq(0.5, BinaryOperator.DIVISION.apply(double1, double2)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.DIVISION.apply(stringA, stringB)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.DIVISION.apply(stringA, int0)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.DIVISION.apply(stringA, boolTrue)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.DIVISION.apply(int0, stringA)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.DIVISION.apply(boolFalse, stringA)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.DIVISION.apply(boolTrue, boolFalse))
        );
    }

    @Test
    void operatorExponentiation() {
        assertAll(
                () -> assertDoubleEq(4.0, BinaryOperator.EXPONENTIATION.apply(int2, int2)),
                () -> assertDoubleEq(4.0, BinaryOperator.EXPONENTIATION.apply(int2, int2)),
                () -> assertDoubleEq(4.0, BinaryOperator.EXPONENTIATION.apply(int2, double2)),
                () -> assertDoubleEq(4.0, BinaryOperator.EXPONENTIATION.apply(double2, int2)),
                () -> assertDoubleEq(4.0, BinaryOperator.EXPONENTIATION.apply(double2, double2)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.EXPONENTIATION.apply(stringA, stringB)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.EXPONENTIATION.apply(stringA, int0)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.EXPONENTIATION.apply(stringA, boolTrue)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.EXPONENTIATION.apply(int0, stringA)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.EXPONENTIATION.apply(boolFalse, stringA)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.EXPONENTIATION.apply(boolTrue, boolFalse))
        );
    }

    @Test
    void operatorModulo() {
        assertAll(
                () -> assertIntEq(1, BinaryOperator.MODULO.apply(int1, int2)),
                () -> assertDoubleEq(1.0, BinaryOperator.MODULO.apply(int1, double2)),
                () -> assertDoubleEq(1.0, BinaryOperator.MODULO.apply(double1, int2)),
                () -> assertDoubleEq(1.0, BinaryOperator.MODULO.apply(double1, double2)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.MODULO.apply(stringA, stringB)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.MODULO.apply(stringA, int0)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.MODULO.apply(stringA, boolTrue)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.MODULO.apply(int0, stringA)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.MODULO.apply(boolFalse, stringA)),
                () -> assertThrows(UnsupportedOperationException.class, () -> BinaryOperator.MODULO.apply(boolTrue, boolFalse))
        );
    }
}