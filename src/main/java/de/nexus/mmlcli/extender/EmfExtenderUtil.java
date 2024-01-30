package de.nexus.mmlcli.extender;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.*;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class EmfExtenderUtil {
    private static final String IDENTIFIER_CLASS_NAME = "MMLConstraintIdentifier";
    private static final String IDENTIFIER_ATTRIBUTE_NAME = "nodeId";

    public static ResourceSet getResourceSet() {
        // Create a resource set.
        ResourceSet resourceSet = new ResourceSetImpl();

        // Register the default resource factory -- only needed for stand-alone!
        resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());
        resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());

        // Register the package -- only needed for stand-alone!
        EcorePackage ecorePackage = EcorePackage.eINSTANCE;

        return resourceSet;
    }

    public static EPackage loadMetaModel(ResourceSet rSet, File metaModelSource) {
        // Get the URI of the model file.
        URI fileURI = URI.createFileURI(metaModelSource.toString());

        // Demand load the resource for this file.
        Resource resource = rSet.getResource(fileURI, true);

        EObject rootElement = resource.getContents().get(0);

        if (rootElement instanceof EPackage ePackage) {
            rSet.getPackageRegistry().put(ePackage.getNsURI(), ePackage);
            return ePackage;
        }
        return null;
    }

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
                throw new IllegalStateException(String.format("%s not no EClass!", IDENTIFIER_CLASS_NAME));
            }
        }

    }

    public static Resource loadModel(ResourceSet rSet, File modelSource) {
        // Get the URI of the model file.
        URI fileURI = URI.createFileURI(modelSource.toString());

        // Demand load the resource for this file.
        return rSet.getResource(fileURI, true);
    }

    public static void extendModel(EPackage ePackage, Resource resource) {
        EClass identifierClass = EmfExtenderUtil.getMMLIdentifierClass(ePackage);
        if (identifierClass == null) {
            throw new IllegalStateException("Trying to extend model without extended metamodel");
        }

        EAttribute idAttribute = (EAttribute) identifierClass.getEStructuralFeature(IDENTIFIER_ATTRIBUTE_NAME);

        AtomicInteger counter = new AtomicInteger(0);

        resource.getContents().forEach(obj -> obj.eSet(idAttribute, counter.incrementAndGet()));
    }

    public static void unextendModel(EPackage ePackage, Resource resource) {
        EClass identifierClass = EmfExtenderUtil.getMMLIdentifierClass(ePackage);
        if (identifierClass == null) {
            throw new IllegalStateException("Trying to extend model without extended metamodel");
        }

        EAttribute idAttribute = (EAttribute) identifierClass.getEStructuralFeature(IDENTIFIER_ATTRIBUTE_NAME);

        resource.getContents().forEach(obj -> obj.eUnset(idAttribute));
    }

    public static void extendMetaModel(EPackage ePackage) {
        EClass identifierClass = EmfExtenderUtil.getMMLIdentifierClass(ePackage);
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
        EClass identifierClass = EmfExtenderUtil.getMMLIdentifierClass(ePackage);
        if (identifierClass == null) {
            return;
        }

        ePackage.getEClassifiers().stream().filter(x -> x instanceof EClass).filter(x -> !((EClass) x).getESuperTypes().isEmpty()).forEach(x -> ((EClass) x).getESuperTypes().remove(identifierClass));

        ePackage.getEClassifiers().remove(identifierClass);
    }

    public static boolean writeFiles(ResourceSet rSet, EPackage metamodel, Resource model, File ecoreFile, File modelFile) {
        Resource resource = rSet.createResource(URI.createFileURI(ecoreFile.toString()));
        resource.getContents().add(metamodel);

        Resource resource2 = rSet.createResource(URI.createFileURI(modelFile.toString()));

        resource2.getContents().addAll(EcoreUtil.copyAll(model.getContents()));

        try {
            resource.save(Collections.emptyMap());
            resource2.save(Collections.emptyMap());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    public static boolean extendToFile(ResourceSet rSet, EPackage metamodel, File ecoreFile, File modelFile) {
        File extEcoreFile = createExtendedFilePath(ecoreFile);
        File extModelFile = createExtendedFilePath(modelFile);

        extendMetaModel(metamodel);

        Resource model = EmfExtenderUtil.loadModel(rSet, modelFile);

        extendModel(metamodel, model);

        return writeFiles(rSet, metamodel, model, extEcoreFile, extModelFile);
    }

    public static boolean unextendToFile(ResourceSet rSet, EPackage metamodel, File ecoreFile, File modelFile) {
        File unextEcoreFile = createUnextendedFilePath(ecoreFile);
        File unextModelFile = createUnextendedFilePath(modelFile);

        unextendMetaModel(metamodel);

        Resource model = EmfExtenderUtil.loadModel(rSet, modelFile);

        unextendModel(metamodel, model);

        return writeFiles(rSet, metamodel, model, unextEcoreFile, unextModelFile);
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

    public static boolean extendFromFileToFile(File ecoreFile, File modelFile) {
        ResourceSet rSet = EmfExtenderUtil.getResourceSet();
        EPackage metaModel = EmfExtenderUtil.loadMetaModel(rSet, ecoreFile);
        if (metaModel == null) {
            throw new IllegalStateException("Unable to load metamodel!");
        }
        return extendToFile(rSet, metaModel, ecoreFile, modelFile);
    }

    public static boolean unextendFromFileToFile(File ecoreFile, File modelFile) {
        ResourceSet rSet = EmfExtenderUtil.getResourceSet();
        EPackage metaModel = EmfExtenderUtil.loadMetaModel(rSet, ecoreFile);
        if (metaModel == null) {
            throw new IllegalStateException("Unable to load metamodel!");
        }
        return unextendToFile(rSet, metaModel, ecoreFile, modelFile);
    }
}
