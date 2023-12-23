package de.nexus.mmlcli.entities.model;

import de.nexus.mmlcli.serializer.EcoreIdResolver;
import org.eclipse.emf.ecore.EReference;

import java.util.UUID;

/**
 * Dataclass for class references
 */
public class CReferenceEntity {
    private String referenceId;
    private String name;
    private MultiplicityEntity multiplicity;
    private String type;
    private ClassElementModifiers modifiers;
    private boolean hasOpposite;
    private String opposite;

    public String getReferenceId() {
        return referenceId;
    }

    public String getName() {
        return name;
    }

    public MultiplicityEntity getMultiplicity() {
        return multiplicity;
    }

    public String getType() {
        return type;
    }

    public ClassElementModifiers getModifiers() {
        return modifiers;
    }

    public boolean isHasOpposite() {
        return hasOpposite;
    }

    public String getOpposite() {
        return opposite;
    }

    private void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    private void setName(String name) {
        this.name = name;
    }

    private void setMultiplicity(MultiplicityEntity multiplicity) {
        this.multiplicity = multiplicity;
    }

    private void setType(String type) {
        this.type = type;
    }

    private void setModifiers(ClassElementModifiers modifiers) {
        this.modifiers = modifiers;
    }

    private void setHasOpposite(boolean hasOpposite) {
        this.hasOpposite = hasOpposite;
    }

    private void setOpposite(String opposite) {
        this.opposite = opposite;
    }

    @Override
    public String toString() {
        return String.format("(%s -> %s)", name, type);
    }

    public static CReferenceEntity fromEReference(EReference eRef, EcoreIdResolver idResolver) {
        CReferenceEntity refEntity = new CReferenceEntity();
        UUID uuid = idResolver.resolveId(eRef);
        refEntity.setReferenceId(uuid.toString());
        refEntity.setName(eRef.getName());
        refEntity.setType(idResolver.resolveId(eRef.getEType()).toString());
        MultiplicityEntity mult;
        if (eRef.getUpperBound() == -1 && eRef.getLowerBound() == 0) {
            mult = new MultiplicityEntity(false, false, true, false, false, 0, 0);
        } else if (eRef.getUpperBound() == -1 && eRef.getLowerBound() == 1) {
            mult = new MultiplicityEntity(false, true, false, false, false, 0, 0);
        } else if (eRef.getUpperBound() == -1 && eRef.getLowerBound() > 0) {
            mult = new MultiplicityEntity(true, false, false, true, false, eRef.getLowerBound(), 0);
        } else if (eRef.getUpperBound() != -1) {
            mult = new MultiplicityEntity(true, false, false, false, false, eRef.getLowerBound(), eRef.getUpperBound());
        } else {
            throw new IllegalStateException(String.format("Unknown multiplicity interpretation (%d|%d)", eRef.getLowerBound(), eRef.getUpperBound()));
        }
        refEntity.setMultiplicity(mult);


        if (eRef.getEOpposite() != null) {
            refEntity.setHasOpposite(true);
            refEntity.setOpposite(idResolver.resolveId(eRef.getEOpposite()).toString());
        } else {
            refEntity.setHasOpposite(false);
        }

        ClassElementModifiers modifiers = new ClassElementModifiers(!eRef.isChangeable(), eRef.isVolatile(), eRef.isTransient(), eRef.isUnsettable(), eRef.isDerived(), eRef.isUnique(), eRef.isOrdered(), eRef.isResolveProxies(), false);
        refEntity.setModifiers(modifiers);

        return refEntity;
    }
}
