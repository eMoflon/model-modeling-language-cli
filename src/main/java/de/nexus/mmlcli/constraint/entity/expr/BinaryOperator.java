package de.nexus.mmlcli.constraint.entity.expr;

import hipe.pattern.ComparatorType;

public enum BinaryOperator {
    EQUALS(ComparatorType.EQUAL),
    NOT_EQUALS(ComparatorType.UNEQUAL),
    GREATER_THAN(ComparatorType.GREATER),
    LESS_THAN(ComparatorType.LESS),
    GREATER_EQUAL_THAN(ComparatorType.GREATER_OR_EQUAL),
    LESS_EQUAL_THAN(ComparatorType.LESS_OR_EQUAL),
    LOGICAL_AND,
    LOGICAL_OR;

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
}
