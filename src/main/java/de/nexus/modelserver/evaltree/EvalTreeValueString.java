package de.nexus.modelserver.evaltree;

public class EvalTreeValueString implements EvalTreeValue {
    private final String value;

    public EvalTreeValueString(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.format("EvalTreeValueString(value = %s)", this.value);
    }
}
