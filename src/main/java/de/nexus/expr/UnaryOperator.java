package de.nexus.expr;

public enum UnaryOperator {
    NEGATION {
        @Override
        public ValueWrapper<?> apply(ValueWrapper<?> a) {
            return a.neg();
        }
    };

    public ValueWrapper<?> apply(ValueWrapper<?> a) {
        throw new UnsupportedOperationException(String.format("UnaryOperation not implemented for Operator: %s", this.name()));
    }
}
