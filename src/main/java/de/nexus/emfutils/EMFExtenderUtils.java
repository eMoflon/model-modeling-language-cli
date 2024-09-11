package de.nexus.emfutils;

import org.eclipse.emf.ecore.*;
import org.eclipse.emf.ecore.resource.Resource;

import java.io.File;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Unique node identifiers are required for constraint evaluation.
 * Therefore, EMFExtender can extend metamodels with a superclass, giving all elements a node identifier.
 * This is initialized for all model elements.
 */
public class EMFExtenderUtils {
    public static final String IDENTIFIER_CLASS_NAME = "MMLConstraintIdentifier";
    public static final String IDENTIFIER_ATTRIBUTE_NAME = "nodeId";

    /**
     * Find the MMLIdentifier class in a package
     *
     * @param ePackage the root package
     * @return the identifier EClass
     */
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

    /**
     * Initialize the identifier attribute for all model elements.
     *
     * @param ePackage the root package
     * @param resource the model resource
     */
    public static void extendModel(EPackage ePackage, Resource resource) {
        EClass identifierClass = EMFExtenderUtils.getMMLIdentifierClass(ePackage);
        if (identifierClass == null) {
            throw new IllegalStateException("Trying to extend model without extended metamodel");
        }

        EAttribute idAttribute = (EAttribute) identifierClass.getEStructuralFeature(IDENTIFIER_ATTRIBUTE_NAME);

        AtomicInteger counter = new AtomicInteger(0);

        resource.getContents().forEach(obj -> obj.eSet(idAttribute, counter.incrementAndGet()));
    }

    /**
     * Unset identifier attribute for all model elements.
     *
     * @param ePackage the root package
     * @param resource the model resource
     */
    public static void unextendModel(EPackage ePackage, Resource resource) {
        EClass identifierClass = EMFExtenderUtils.getMMLIdentifierClass(ePackage);
        if (identifierClass == null) {
            throw new IllegalStateException("Trying to extend model without extended metamodel");
        }

        EAttribute idAttribute = (EAttribute) identifierClass.getEStructuralFeature(IDENTIFIER_ATTRIBUTE_NAME);

        resource.getContents().forEach(obj -> obj.eUnset(idAttribute));
    }

    /**
     * Add the identifier class to the metamodel.
     *
     * @param ePackage the root package
     */
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

    /**
     * Add the identifier class as a superclass to every metamodel element.
     *
     * @param ePackage        the root class
     * @param identifierClass the identifier class
     */
    private static void extendClassesInPackage(EPackage ePackage, EClass identifierClass) {
        ePackage.getEClassifiers().stream().filter(x -> x instanceof EClass).filter(x -> ((EClass) x).getESuperTypes().isEmpty()).forEach(x -> ((EClass) x).getESuperTypes().add(identifierClass));
        ePackage.getESubpackages().forEach(subPackage -> extendClassesInPackage(subPackage, identifierClass));
    }

    /**
     * Remove the identifier class from the metamodel.
     *
     * @param ePackage the root package
     */
    public static void unextendMetaModel(EPackage ePackage) {
        EClass identifierClass = EMFExtenderUtils.getMMLIdentifierClass(ePackage);
        if (identifierClass == null) {
            return;
        }

        ePackage.getEClassifiers().stream().filter(x -> x instanceof EClass).filter(x -> !((EClass) x).getESuperTypes().isEmpty()).forEach(x -> ((EClass) x).getESuperTypes().remove(identifierClass));

        ePackage.getEClassifiers().remove(identifierClass);
    }

    /**
     * Create copies of a metamodel and a model and store then in an given location.
     *
     * @param emfLoader EMFLoader
     * @param metamodel root package of the metamodel
     * @param model     resource of the model
     * @param ecoreFile path for the metamodel copy
     * @param modelFile path for the model copy
     * @return EMFExtenderResult indicating the success
     */
    public static EMFExtenderResult writeFiles(IEMFLoader emfLoader, EPackage metamodel, Resource model, File ecoreFile, File modelFile) {
        Resource ecoreResource = emfLoader.copyResource(metamodel, ecoreFile);

        Resource xmiResource = emfLoader.copyResource(model, modelFile);

        if (!emfLoader.saveResource(ecoreResource)) {
            return new EMFExtenderResult(false, ecoreFile, modelFile);
        }
        boolean result = emfLoader.saveResource(xmiResource);
        return new EMFExtenderResult(result, ecoreFile, modelFile);
    }

    /**
     * Extend a metamodel and a model and store them to file
     *
     * @param emfLoader EMFLoader
     * @param metamodel root package of the metamodel
     * @param ecoreFile path of the metamodel
     * @param modelFile path of the model
     * @return EMFExtenderResult indicating the success
     */
    public static EMFExtenderResult extendToFile(IEMFLoader emfLoader, EPackage metamodel, File ecoreFile, File modelFile) {
        File extEcoreFile = createExtendedFilePath(ecoreFile);
        File extModelFile = createExtendedFilePath(modelFile);

        extendMetaModel(metamodel);

        Resource model = emfLoader.loadResource(modelFile);

        extendModel(metamodel, model);

        return writeFiles(emfLoader, metamodel, model, extEcoreFile, extModelFile);
    }

    /**
     * Unextend a metamodel and a model and store them to file.
     *
     * @param emfLoader EMFLoader
     * @param metamodel root package of the metamodel
     * @param ecoreFile path of the metamodel
     * @param modelFile path of the model
     * @return EMFExtenderResult indicating the success
     */
    public static EMFExtenderResult unextendToFile(IEMFLoader emfLoader, EPackage metamodel, File ecoreFile, File modelFile) {
        File unextEcoreFile = createUnextendedFilePath(ecoreFile);
        File unextModelFile = createUnextendedFilePath(modelFile);

        unextendMetaModel(metamodel);

        Resource model = emfLoader.loadResource(modelFile);

        unextendModel(metamodel, model);

        return writeFiles(emfLoader, metamodel, model, unextEcoreFile, unextModelFile);
    }

    /**
     * Create the filepath for an extended metamodel or model.
     * Added an ext suffix in front of the file extension.
     *
     * @param file original path
     * @return updated path
     * @throws IllegalArgumentException when the passed filetype is not ecore or xmi
     */
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

    /**
     * Create the filepath for an unextended metamodel or model.
     * Removes an ext suffix in front of the file extension.
     *
     * @param file original path
     * @return updated path
     * @throws IllegalArgumentException when the passed filetype is not ecore or xmi
     */
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

    /**
     * Extend a metamodel and a model.
     * The extended metamode and model are stored in the original directory with suffixed filename.
     *
     * @param ecoreFile path to the metamodel
     * @param modelFile path to the model
     * @return EMFExtenderResult indicating success
     */
    public static EMFExtenderResult extendFromFileToFile(File ecoreFile, File modelFile) {
        EMFLoader emfLoader = new EMFLoader();
        EPackage metaModel = emfLoader.loadResourceAsPackage(ecoreFile);
        if (metaModel == null) {
            throw new IllegalStateException("Unable to load metamodel!");
        }
        return extendToFile(emfLoader, metaModel, ecoreFile, modelFile);
    }

    /**
     * Unextend a metamodel and a model.
     * The unextended metamode and model are stored in the original directory with filename without suffix.
     *
     * @param ecoreFile path to the metamodel
     * @param modelFile path to the model
     * @return EMFExtenderResult indicating success
     */
    public static EMFExtenderResult unextendFromFileToFile(File ecoreFile, File modelFile) {
        EMFLoader emfLoader = new EMFLoader();
        EPackage metaModel = emfLoader.loadResourceAsPackage(ecoreFile);
        if (metaModel == null) {
            throw new IllegalStateException("Unable to load metamodel!");
        }
        return unextendToFile(emfLoader, metaModel, ecoreFile, modelFile);
    }
}

