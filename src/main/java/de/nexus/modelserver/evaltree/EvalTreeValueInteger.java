package de.nexus.modelserver.evaltree;

public class EvalTreeValueInteger implements EvalTreeValue {
    private final int value;

    public EvalTreeValueInteger(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.format("EvalTreeValueInteger(value = %d)", this.value);
    }
}
