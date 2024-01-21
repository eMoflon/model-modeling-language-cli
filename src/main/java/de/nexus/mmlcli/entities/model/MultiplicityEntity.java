package de.nexus.mmlcli.entities.model;

/**
 * Dataclass for reference multiplicities
 */
public class MultiplicityEntity {
    private final boolean hasUpperBound;
    private final boolean lowerIsN;
    private final boolean lowerIsN0;
    private final boolean upperIsN;
    private final boolean upperIsN0;
    private final int lower;
    private final int upper;

    public boolean isHasUpperBound() {
        return hasUpperBound;
    }

    public boolean isLowerIsN() {
        return lowerIsN;
    }

    public boolean isLowerIsN0() {
        return lowerIsN0;
    }

    public boolean isUpperIsN() {
        return upperIsN;
    }

    public boolean isUpperIsN0() {
        return upperIsN0;
    }

    public int getLower() {
        return lower;
    }

    public int getUpper() {
        return upper;
    }

    public MultiplicityEntity(boolean hasUpperBound, boolean lowerIsN, boolean lowerIsN0, boolean upperIsN, boolean upperIsN0, int lower, int upper) {
        this.hasUpperBound = hasUpperBound;
        this.lowerIsN = lowerIsN;
        this.lowerIsN0 = lowerIsN0;
        this.upperIsN = upperIsN;
        this.upperIsN0 = upperIsN0;
        this.lower = lower;
        this.upper = upper;
    }
}
