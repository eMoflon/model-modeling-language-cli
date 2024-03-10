package de.nexus.emfutils;

import org.eclipse.emf.ecore.*;
import org.eclipse.emf.ecore.resource.Resource;

import java.io.File;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class EMFExtenderUtils {
    public static final String IDENTIFIER_CLASS_NAME = "MMLConstraintIdentifier";
    public static final String IDENTIFIER_ATTRIBUTE_NAME = "nodeId";

    public static EClass getMMLIdentifierClass(EPackage ePackage) {
        List<EClassifier> classifiers = ePackage.getEClassifiers().stream().filter(x -> x instanceof EClass).filter(x -> ((EClass) x).isAbstract() && ((EClass) x).getESuperTypes().isEmpty() && x.getName().equals(IDENTIFIER_CLASS_NAME)).toList();
        if (classifiers.isEmpty()) {
            return null;
        } else if (classifiers.size() > 1) {
            throw new IllegalStateException(String.format("There are multiple %s Classes contained!", IDENTIFIER_CLASS_NAME));
        } else {
            if (classifiers.get(0) instanceof EClass clazz) {
                return clazz;
            } else {
                throw new IllegalStateException(String.format("%s is no EClass!", IDENTIFIER_CLASS_NAME));
            }
        }

    }

    public static void extendModel(EPackage ePackage, Resource resource) {
        EClass identifierClass = EMFExtenderUtils.getMMLIdentifierClass(ePackage);
        if (identifierClass == null) {
            throw new IllegalStateException("Trying to extend model without extended metamodel");
        }

        EAttribute idAttribute = (EAttribute) identifierClass.getEStructuralFeature(IDENTIFIER_ATTRIBUTE_NAME);

        AtomicInteger counter = new AtomicInteger(0);

        resource.getContents().forEach(obj -> obj.eSet(idAttribute, counter.incrementAndGet()));
    }

    public static void unextendModel(EPackage ePackage, Resource resource) {
        EClass identifierClass = EMFExtenderUtils.getMMLIdentifierClass(ePackage);
        if (identifierClass == null) {
            throw new IllegalStateException("Trying to extend model without extended metamodel");
        }

        EAttribute idAttribute = (EAttribute) identifierClass.getEStructuralFeature(IDENTIFIER_ATTRIBUTE_NAME);

        resource.getContents().forEach(obj -> obj.eUnset(idAttribute));
    }

    public static void extendMetaModel(EPackage ePackage) {
        EClass identifierClass = EMFExtenderUtils.getMMLIdentifierClass(ePackage);
        if (identifierClass != null) {
            return;
        }

        EClass clazz = EcoreFactory.eINSTANCE.createEClass();
        clazz.setAbstract(true);
        clazz.setName(IDENTIFIER_CLASS_NAME);

        EAttribute idAttribute = EcoreFactory.eINSTANCE.createEAttribute();
        idAttribute.setName(IDENTIFIER_ATTRIBUTE_NAME);
        idAttribute.setLowerBound(0);
        idAttribute.setUpperBound(1);
        idAttribute.setEType(EcorePackage.Literals.EINT);

        clazz.getEStructuralFeatures().add(idAttribute);

        extendClassesInPackage(ePackage, clazz);

        ePackage.getEClassifiers().add(clazz);
    }

    private static void extendClassesInPackage(EPackage ePackage, EClass identifierClass) {
        ePackage.getEClassifiers().stream().filter(x -> x instanceof EClass).filter(x -> ((EClass) x).getESuperTypes().isEmpty()).forEach(x -> ((EClass) x).getESuperTypes().add(identifierClass));
        ePackage.getESubpackages().forEach(subPackage -> extendClassesInPackage(subPackage, identifierClass));
    }

    public static void unextendMetaModel(EPackage ePackage) {
        EClass identifierClass = EMFExtenderUtils.getMMLIdentifierClass(ePackage);
        if (identifierClass == null) {
            return;
        }

        ePackage.getEClassifiers().stream().filter(x -> x instanceof EClass).filter(x -> !((EClass) x).getESuperTypes().isEmpty()).forEach(x -> ((EClass) x).getESuperTypes().remove(identifierClass));

        ePackage.getEClassifiers().remove(identifierClass);
    }

    public static EMFExtenderResult writeFiles(IEMFLoader emfLoader, EPackage metamodel, Resource model, File ecoreFile, File modelFile) {
        Resource ecoreResource = emfLoader.copyResource(metamodel, ecoreFile);

        Resource xmiResource = emfLoader.copyResource(model, modelFile);

        if (!emfLoader.saveResource(ecoreResource)) {
            return new EMFExtenderResult(false, ecoreFile, modelFile);
        }
        boolean result = emfLoader.saveResource(xmiResource);
        return new EMFExtenderResult(result, ecoreFile, modelFile);
    }

    public static EMFExtenderResult extendToFile(IEMFLoader emfLoader, EPackage metamodel, File ecoreFile, File modelFile) {
        File extEcoreFile = createExtendedFilePath(ecoreFile);
        File extModelFile = createExtendedFilePath(modelFile);

        extendMetaModel(metamodel);

        Resource model = emfLoader.loadResource(modelFile);

        extendModel(metamodel, model);

        return writeFiles(emfLoader, metamodel, model, extEcoreFile, extModelFile);
    }

    public static EMFExtenderResult unextendToFile(IEMFLoader emfLoader, EPackage metamodel, File ecoreFile, File modelFile) {
        File unextEcoreFile = createUnextendedFilePath(ecoreFile);
        File unextModelFile = createUnextendedFilePath(modelFile);

        unextendMetaModel(metamodel);

        Resource model = emfLoader.loadResource(modelFile);

        unextendModel(metamodel, model);

        return writeFiles(emfLoader, metamodel, model, unextEcoreFile, unextModelFile);
    }

    public static File createExtendedFilePath(File file) {
        if (file.getAbsolutePath().endsWith(".ext.ecore")) {
            return file;
        } else if (file.getAbsolutePath().endsWith(".ecore")) {
            return new File(file.toString().replace(".ecore", ".ext.ecore"));
        } else if (file.getAbsolutePath().endsWith(".ext.xmi")) {
            return file;
        } else if (file.getAbsolutePath().endsWith(".xmi")) {
            return new File(file.toString().replace(".xmi", ".ext.xmi"));
        }
        throw new IllegalArgumentException("Passed file must be ecore or xmi: " + file.getAbsolutePath());
    }

    public static File createUnextendedFilePath(File file) {
        if (file.getAbsolutePath().endsWith(".ext.ecore")) {
            return new File(file.toString().replace(".ext.ecore", ".ecore"));
        } else if (file.getAbsolutePath().endsWith(".ecore")) {
            return file;
        } else if (file.getAbsolutePath().endsWith(".ext.xmi")) {
            return new File(file.toString().replace(".ext.xmi", ".xmi"));
        } else if (file.getAbsolutePath().endsWith(".xmi")) {
            return file;
        }
        throw new IllegalArgumentException("Passed file must be ecore or xmi: " + file.getAbsolutePath());
    }

    public static EMFExtenderResult extendFromFileToFile(File ecoreFile, File modelFile) {
        EMFLoader emfLoader = new EMFLoader();
        EPackage metaModel = emfLoader.loadResourceAsPackage(ecoreFile);
        if (metaModel == null) {
            throw new IllegalStateException("Unable to load metamodel!");
        }
        return extendToFile(emfLoader, metaModel, ecoreFile, modelFile);
    }

    public static EMFExtenderResult unextendFromFileToFile(File ecoreFile, File modelFile) {
        EMFLoader emfLoader = new EMFLoader();
        EPackage metaModel = emfLoader.loadResourceAsPackage(ecoreFile);
        if (metaModel == null) {
            throw new IllegalStateException("Unable to load metamodel!");
        }
        return unextendToFile(emfLoader, metaModel, ecoreFile, modelFile);
    }
}

