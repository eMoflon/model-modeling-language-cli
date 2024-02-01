package de.nexus.mmlcli.constraint.entity;

public enum NodeConstraintOperator {
    EQUAL,
    UNEQUAL;

    public static NodeConstraintOperator fromStringOperator(String op) {
        if (op.equals("==")) {
            return EQUAL;
        } else if (op.equals("!=")) {
            return UNEQUAL;
        }
        throw new RuntimeException("Unknown NodeContraintOperator string: " + op);
    }

    public String toStringOperator() {
        return switch (this) {
            case EQUAL -> "==";
            case UNEQUAL -> "!=";
        };
    }
}
