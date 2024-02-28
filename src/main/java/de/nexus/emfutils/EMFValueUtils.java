package de.nexus.emfutils;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EcorePackage;

public class EMFValueUtils {
    @SuppressWarnings("unchecked")
    public static <T, R> R mapVals(EClassifier type, T value) {
        return switch (type.getClassifierID()) {
            case EcorePackage.ESTRING -> (R) value;
            case EcorePackage.EFLOAT -> (R) Float.valueOf(value.toString());
            case EcorePackage.EDOUBLE -> (R) Double.valueOf(value.toString());
            case EcorePackage.EINT -> (R) Integer.valueOf(Double.valueOf(value.toString()).intValue());
            case EcorePackage.EBOOLEAN -> (R) Boolean.valueOf(value.toString());
            default -> (R) value.toString();
        };
    }
}
