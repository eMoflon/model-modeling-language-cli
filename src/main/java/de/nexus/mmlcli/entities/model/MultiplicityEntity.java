package de.nexus.mmlcli.entities.model;

/**
 * Dataclass for reference multiplicities
 */
public class MultiplicityEntity {
    private boolean hasUpperBound;
    private boolean lowerIsN;
    private boolean lowerIsN0;
    private boolean upperIsN;
    private boolean upperIsN0;
    private int lower;
    private int upper;

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
