package de.nexus.modelserver.evaltree;

public class EvalTreeValueBoolean implements EvalTreeValue {
    private final boolean value;

    public EvalTreeValueBoolean(boolean value) {
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }
}
