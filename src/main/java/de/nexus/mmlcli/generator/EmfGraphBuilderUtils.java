package de.nexus.mmlcli.generator;

import de.nexus.emfutils.EMFValueUtils;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EcorePackage;

public class EmfGraphBuilderUtils {
    public static EDataType mapETypes(String mmlType) {
        return switch (mmlType) {
            case "string" -> EcorePackage.Literals.ESTRING;
            case "float" -> EcorePackage.Literals.EFLOAT;
            case "double" -> EcorePackage.Literals.EDOUBLE;
            case "int" -> EcorePackage.Literals.EINT;
            case "boolean" -> EcorePackage.Literals.EBOOLEAN;
            default -> EcorePackage.Literals.ESTRING;
        };
    }

    public static String mapETypes(EDataType dataType) {
        return switch (dataType.getClassifierID()) {
            case EcorePackage.EFLOAT -> "float";
            case EcorePackage.EDOUBLE -> "double";
            case EcorePackage.EINT -> "int";
            case EcorePackage.EBOOLEAN -> "boolean";
            default -> "string";
        };
    }

    public static <T> boolean isETypeDefaultValue(EDataType type, T value) {
        return type.getDefaultValue().equals(value);
    }

    public static <T, R> R mapVals(String mmlType, T value) {
        return EMFValueUtils.mapVals(mapETypes(mmlType), value);
    }
}
