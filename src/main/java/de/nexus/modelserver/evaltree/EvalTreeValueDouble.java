package de.nexus.modelserver.evaltree;

public class EvalTreeValueDouble implements EvalTreeValue {
    private final double value;

    public EvalTreeValueDouble(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }
}
