package de.nexus.expr;

import hipe.pattern.ComparatorType;

public enum BinaryOperator {
    EQUALS(ComparatorType.EQUAL) {
        @Override
        public ValueWrapper<?> apply(ValueWrapper<?> a, ValueWrapper<?> b) {
            return a.equals(b);
        }
    },
    NOT_EQUALS(ComparatorType.UNEQUAL) {
        @Override
        public ValueWrapper<?> apply(ValueWrapper<?> a, ValueWrapper<?> b) {
            return a.notEquals(b);
        }
    },
    GREATER_THAN(ComparatorType.GREATER) {
        @Override
        public ValueWrapper<?> apply(ValueWrapper<?> a, ValueWrapper<?> b) {
            return a.greater(b);
        }
    },
    LESS_THAN(ComparatorType.LESS) {
        @Override
        public ValueWrapper<?> apply(ValueWrapper<?> a, ValueWrapper<?> b) {
            return a.less(b);
        }
    },
    GREATER_EQUAL_THAN(ComparatorType.GREATER_OR_EQUAL) {
        @Override
        public ValueWrapper<?> apply(ValueWrapper<?> a, ValueWrapper<?> b) {
            return a.greaterEq(b);
        }
    },
    LESS_EQUAL_THAN(ComparatorType.LESS_OR_EQUAL) {
        @Override
        public ValueWrapper<?> apply(ValueWrapper<?> a, ValueWrapper<?> b) {
            return a.lessEq(b);
        }
    },
    LOGICAL_AND() {
        @Override
        public ValueWrapper<?> apply(ValueWrapper<?> a, ValueWrapper<?> b) {
            return a.and(b);
        }
    },
    LOGICAL_OR() {
        @Override
        public ValueWrapper<?> apply(ValueWrapper<?> a, ValueWrapper<?> b) {
            return a.or(b);
        }
    },
    ADDITION() {
        @Override
        public ValueWrapper<?> apply(ValueWrapper<?> a, ValueWrapper<?> b) throws UnsupportedOperationException {
            return a.add(b);
        }
    },
    SUBTRACTION() {
        @Override
        public ValueWrapper<?> apply(ValueWrapper<?> a, ValueWrapper<?> b) {
            return a.sub(b);
        }
    },
    MULTIPLICATION() {
        @Override
        public ValueWrapper<?> apply(ValueWrapper<?> a, ValueWrapper<?> b) {
            return a.mult(b);
        }
    },
    DIVISION() {
        @Override
        public ValueWrapper<?> apply(ValueWrapper<?> a, ValueWrapper<?> b) {
            return a.div(b);
        }
    },
    EXPONENTIATION() {
        @Override
        public ValueWrapper<?> apply(ValueWrapper<?> a, ValueWrapper<?> b) {
            return a.pow(b);
        }
    },
    MODULO() {
        @Override
        public ValueWrapper<?> apply(ValueWrapper<?> a, ValueWrapper<?> b) {
            return a.mod(b);
        }
    },
    ;

    private final ComparatorType hipeType;

    BinaryOperator(ComparatorType hipeType) {
        this.hipeType = hipeType;
    }

    BinaryOperator() {
        this.hipeType = null;
    }

    public ComparatorType asHiPEComparatorType() {
        return this.hipeType;
    }

    public boolean isHiPEComparator() {
        return this.hipeType != null;
    }

    public ValueWrapper<?> apply(ValueWrapper<?> a, ValueWrapper<?> b) {
        throw new UnsupportedOperationException(String.format("BinaryOperation not implemented for Operator: %s", this.name()));
    }
}
