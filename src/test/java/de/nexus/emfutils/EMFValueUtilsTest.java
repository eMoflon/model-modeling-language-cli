package de.nexus.emfutils;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EcorePackage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class EMFValueUtilsTest {

    @Test
    void mapEStringVal() {
        EClassifier eType = EcorePackage.Literals.ESTRING;
        Object value = "TestValue";

        assertInstanceOf(String.class, EMFValueUtils.mapVals(eType, value));
        assertEquals("TestValue", EMFValueUtils.mapVals(eType, value));
    }

    @Test
    void mapEFloatVal() {
        EClassifier eType = EcorePackage.Literals.EFLOAT;
        Object value = 4.2f;

        assertInstanceOf(Float.class, EMFValueUtils.mapVals(eType, value));
        assertEquals(4.2f, (Float) EMFValueUtils.mapVals(eType, value));
    }

    @Test
    void mapEDoubleVal() {
        EClassifier eType = EcorePackage.Literals.EDOUBLE;
        Object value = 4.2;

        assertInstanceOf(Double.class, EMFValueUtils.mapVals(eType, value));
        assertEquals(4.2, EMFValueUtils.mapVals(eType, value));
    }

    @Test
    void mapEIntVal() {
        EClassifier eType = EcorePackage.Literals.EINT;
        Object value = 42;

        assertInstanceOf(Integer.class, EMFValueUtils.mapVals(eType, value));
        assertEquals(42, (Integer) EMFValueUtils.mapVals(eType, value));
    }

    @Test
    void mapEBooleanVal() {
        EClassifier eType = EcorePackage.Literals.EBOOLEAN;
        Object value = true;

        assertInstanceOf(Boolean.class, EMFValueUtils.mapVals(eType, value));
        assertEquals(true, EMFValueUtils.mapVals(eType, value));
    }

    @Test
    void mapDefaultVal() {
        EClassifier eType = EcorePackage.Literals.EENUM_LITERAL;
        Object value = "TestLit";

        assertInstanceOf(String.class, EMFValueUtils.mapVals(eType, value));
        assertEquals("TestLit", EMFValueUtils.mapVals(eType, value));
    }
}