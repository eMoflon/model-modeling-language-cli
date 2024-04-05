package de.nexus.emfutils;

import de.nexus.expr.ValueWrapper;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EcorePackage;
import org.emoflon.smartemf.runtime.SmartObject;

public class EMFValueUtils {
    @SuppressWarnings("unchecked")
    public static <T, R> R mapVals(EClassifier type, T value) {
        if (type instanceof EEnum eEnum) {
            return (R) eEnum.getEEnumLiteral(value.toString()).getInstance();
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

    public static ValueWrapper<?> requestValue(SmartObject obj, String attrName) {
        EAttribute attr = (EAttribute) obj.eClass().getEStructuralFeature(attrName);
        if (attr == null) {
            throw new RuntimeException(String.format("Trying to access %s.%s but it does not exist!", obj.eClass().getName(), attrName));
        }
        return ValueWrapper.fromEcoreValue(attr.getEAttributeType(), obj.eGet(attr));
    }
}
