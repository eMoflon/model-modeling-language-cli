package de.nexus.emfutils;

import de.nexus.expr.ValueWrapper;
import org.eclipse.emf.ecore.*;
import org.emoflon.smartemf.runtime.SmartObject;

public class EMFValueUtils {
    /**
     * Cast an object to a Java type based on an EClassifier.
     * Requesting an attribute value from EAttribute returns an object.
     * To cast this to the correct Java type, the attribute type must be used.
     *
     * @param type  attribute type as EClassifier
     * @param value attribute value
     * @param <T>   value type
     * @param <R>   return type
     * @return attribute value with updated type
     */
    @SuppressWarnings("unchecked")
    public static <T, R> R mapVals(EClassifier type, T value) {
        if (type instanceof EEnum eEnum) {
            EEnumLiteral literal = eEnum.getEEnumLiteral(value.toString());
            if (literal == null) {
                return null;
            }
            return (R) literal.getInstance();
        }

        return switch (type.getClassifierID()) {
            case EcorePackage.ESTRING -> (R) value;
            case EcorePackage.EFLOAT -> (R) Float.valueOf(value.toString());
            case EcorePackage.EDOUBLE -> (R) Double.valueOf(value.toString());
            case EcorePackage.EINT -> (R) Integer.valueOf(Double.valueOf(value.toString()).intValue());
            case EcorePackage.EBOOLEAN -> (R) Boolean.valueOf(value.toString());
            default -> (R) value.toString();
        };
    }

    /**
     * Get SmartObject attribute as ValueWrapper
     *
     * @param obj  SmartObject
     * @param attr the requested attribute
     * @return the requested attribute value as ValueWrapper
     */
    public static ValueWrapper<?> requestValue(SmartObject obj, EAttribute attr) {
        return ValueWrapper.fromEcoreValue(attr.getEAttributeType(), obj.eGet(attr));
    }

    /**
     * Get SmartObject attribute as ValueWrapper
     *
     * @param obj      SmartObject
     * @param attrName the requested attribute name
     * @return the requested attribute value as ValueWrapper
     */
    public static ValueWrapper<?> requestValue(SmartObject obj, String attrName) {
        EAttribute attr = (EAttribute) obj.eClass().getEStructuralFeature(attrName);
        if (attr == null) {
            throw new RuntimeException(String.format("Trying to access %s.%s but it does not exist!", obj.eClass().getName(), attrName));
        }
        return EMFValueUtils.requestValue(obj, attr);
    }
}
