package de.nexus.mmlcli.entities.model;

import com.google.gson.annotations.SerializedName;

/**
 * Dataclass for class modifiers
 */
public class ClassElementModifiers {
    private boolean readonly;
    @SerializedName("volatile")
    private boolean _volatile;
    @SerializedName("transient")
    private boolean _transient;
    private boolean unsettable;
    private boolean derived;
    private boolean unique;
    private boolean ordered;
    private boolean resolve;
    private boolean containment;
    private boolean id;

    public boolean isReadonly() {
        return readonly;
    }

    public boolean isVolatile() {
        return _volatile;
    }

    public boolean isTransient() {
        return _transient;
    }

    public boolean isUnsettable() {
        return unsettable;
    }

    public boolean isDerived() {
        return derived;
    }

    public boolean isUnique() {
        return unique;
    }

    public boolean isOrdered() {
        return ordered;
    }

    public boolean isResolve() {
        return resolve;
    }

    public boolean isContainment() {
        return containment;
    }

    public boolean isId() {
        return id;
    }

    public ClassElementModifiers(boolean readonly, boolean _volatile, boolean _transient, boolean unsettable, boolean derived, boolean unique, boolean ordered, boolean resolve, boolean containment, boolean id) {
        this.readonly = readonly;
        this._volatile = _volatile;
        this._transient = _transient;
        this.unsettable = unsettable;
        this.derived = derived;
        this.unique = unique;
        this.ordered = ordered;
        this.resolve = resolve;
        this.containment = containment;
        this.id = id;
    }
}
