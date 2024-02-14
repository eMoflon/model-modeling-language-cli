package de.nexus.expr;

public enum UnaryOperator {
    NEGATION {
        @Override
        public boolean applyBool(boolean a) {
            return !a;
        }
    };

    public boolean applyBool(boolean a) {
        throw new UnsupportedOperationException("");
    }
}
