package de.nexus.mmlcli.entities.model;

import de.nexus.mmlcli.generator.EmfGraphBuilderUtils;
import de.nexus.mmlcli.serializer.EcoreIdResolver;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;

import java.util.UUID;

/**
 * Dataclass for a class attribute
 */
public class AttributeEntity<T> {
    private String referenceId;
    private String name;
    private String type;
    private boolean isEnumType;
    private boolean hasDefaultValue;
    private T defaultValue;
    private ClassElementModifiers modifiers;

    public String getReferenceId() {
        return referenceId;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public boolean isEnumType() {
        return isEnumType;
    }

    public boolean isHasDefaultValue() {
        return hasDefaultValue;
    }

    public T getDefaultValue() {
        return defaultValue;
    }

    public ClassElementModifiers getModifiers() {
        return modifiers;
    }

    private void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    private void setName(String name) {
        this.name = name;
    }

    private void setType(String type) {
        this.type = type;
    }

    private void setEnumType(boolean enumType) {
        isEnumType = enumType;
    }

    private void setHasDefaultValue(boolean hasDefaultValue) {
        this.hasDefaultValue = hasDefaultValue;
    }

    private void setDefaultValue(T defaultValue) {
        this.defaultValue = defaultValue;
    }

    private void setModifiers(ClassElementModifiers modifiers) {
        this.modifiers = modifiers;
    }

    @Override
    public String toString() {
        return String.format("%s<%s>", name, type);
    }

    public static AttributeEntity<?> fromEAttribute(EAttribute eAttribute, EcoreIdResolver idResolver) {
        AttributeEntity<?> attribute = new AttributeEntity<>();
        UUID uuid = idResolver.resolveId(eAttribute);
        attribute.setReferenceId(uuid.toString());
        attribute.setName(eAttribute.getName());
        if (eAttribute.getEType() instanceof EEnum) {
            attribute.setEnumType(true);
            attribute.setType(idResolver.resolveId(eAttribute.getEType()).toString());

            if (eAttribute.getDefaultValue() != null && !EmfGraphBuilderUtils.isETypeDefaultValue((EDataType) eAttribute.getEType(), eAttribute.getDefaultValue())) {
                attribute.setHasDefaultValue(true);
                attribute.setDefaultValue(EmfGraphBuilderUtils.mapVals("-", idResolver.resolveId((EEnumLiteral) eAttribute.getDefaultValue())));
            } else {
                attribute.setHasDefaultValue(false);
            }
        } else if (eAttribute.getEType() instanceof EDataType) {
            attribute.setEnumType(false);
            attribute.setType(EmfGraphBuilderUtils.mapETypes((EDataType) eAttribute.getEType()));

            if (eAttribute.getDefaultValue() != null && !EmfGraphBuilderUtils.isETypeDefaultValue((EDataType) eAttribute.getEType(), eAttribute.getDefaultValue())) {
                attribute.setHasDefaultValue(true);
                attribute.setDefaultValue(EmfGraphBuilderUtils.mapVals(eAttribute.getEAttributeType(), eAttribute.getDefaultValue()));
            } else {
                attribute.setHasDefaultValue(false);
            }
        } else {
            throw new IllegalArgumentException("Unexpected attribute type: " + eAttribute.getEType().toString());
        }

        ClassElementModifiers modifiers = new ClassElementModifiers(!eAttribute.isChangeable(), eAttribute.isVolatile(), eAttribute.isTransient(), eAttribute.isUnsettable(), eAttribute.isDerived(), eAttribute.isUnique(), eAttribute.isOrdered(), false, eAttribute.isID());
        attribute.setModifiers(modifiers);


        return attribute;
    }
}
