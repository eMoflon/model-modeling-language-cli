package de.nexus.modelserver;

import de.nexus.emfutils.EMFExtenderUtils;
import de.nexus.emfutils.SmartEMFLoader;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.emoflon.smartemf.persistence.SmartEMFResource;
import org.emoflon.smartemf.runtime.SmartObject;

import java.io.File;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Optional;
import java.util.Set;

/**
 * The IndexedEMFLoader extends a SmartEMFLoader and creates an index
 * that maps nodeIds to SmartObjects
 */
public class IndexedEMFLoader extends SmartEMFLoader {
    private final HashMap<Integer, SmartObject> idIndex = new HashMap<>();

    private EStructuralFeature idStrucuralFeature = null;
    private int currentId = -1;

    public IndexedEMFLoader(Path workspace) {
        super(workspace);
    }

    /**
     * Initializes the index.
     * Traverses the complete model, requests the nodeId for each node and stores it in the index.
     *
     * @param resource a Resource
     * @throws IllegalStateException if the model is not extended (does not contain unique identifiers)
     */
    private void initializeIndices(SmartEMFResource resource) throws IllegalStateException {
        this.idIndex.clear();
        this.idStrucuralFeature = null;
        this.currentId = -1;

        TreeIterator<EObject> nodeIterator = resource.getAllContents();

        while (nodeIterator.hasNext()) {
            EObject eObject = nodeIterator.next();
            if (idStrucuralFeature == null) {
                Optional<EClass> mmlExtenderClass = eObject.eClass().getEAllSuperTypes().stream().filter(x -> x.getName().equals(EMFExtenderUtils.IDENTIFIER_CLASS_NAME)).findFirst();
                if (mmlExtenderClass.isEmpty()) {
                    throw new IllegalStateException("Could not find extender class: " + EMFExtenderUtils.IDENTIFIER_CLASS_NAME);
                } else {
                    idStrucuralFeature = mmlExtenderClass.get().getEStructuralFeature(EMFExtenderUtils.IDENTIFIER_ATTRIBUTE_NAME);

                    if (idStrucuralFeature == null) {
                        throw new IllegalStateException("Could not find extender feature: " + EMFExtenderUtils.IDENTIFIER_ATTRIBUTE_NAME);
                    }
                }
            }
            this.idIndex.put((int) eObject.eGet(idStrucuralFeature), (SmartObject) eObject);
        }

        this.currentId = Collections.max(this.idIndex.keySet()) + 1;
    }

    /**
     * Initialize a single node.
     * Creates a new nodeId, assignes it to the node and stores it in the index.
     *
     * @param object a SmartObject
     * @return the newly generated nodeId
     */
    public int initializeNode(SmartObject object) {
        int objectId = this.currentId++;
        object.eSet(this.idStrucuralFeature, objectId);
        this.idIndex.put(objectId, object);
        return objectId;
    }

    /**
     * Remove a node from the index.
     *
     * @param object a SmartObject
     */
    public void unregisterNode(SmartObject object) {
        int nodeId = this.getNodeId(object);
        this.idIndex.remove(nodeId);
    }

    /**
     * Get the nodeId of a SmartObject
     *
     * @param object a SmartObject
     * @return the nodeId
     */
    public int getNodeId(SmartObject object) {
        return (int) object.eGet(this.idStrucuralFeature);
    }

    /**
     * Check if a EStructuralFeature is the identifier feature.
     *
     * @param structuralFeature a EStructuralFeature
     * @return if the EStructuralFeature is the identifier feature.
     */
    public boolean isFeatureNodeId(EStructuralFeature structuralFeature) {
        if (this.idStrucuralFeature == null) {
            throw new NullPointerException("Trying to access idStructuralFeature, but it has not been initialized yet!");
        }
        return this.idStrucuralFeature == structuralFeature;
    }

    /**
     * Get a SmartObject by nodeId from the index
     *
     * @param id requested nodeId
     * @return requested SmartObject
     */
    public SmartObject getNode(int id) {
        return this.idIndex.get(id);
    }

    /**
     * Get a set of all assigned nodeIds.
     *
     * @return a set of all assigned nodeIds
     */
    public Set<Integer> getAllNodeIds() {
        return this.idIndex.keySet();
    }

    /**
     * Get the root package of the loaded model.
     *
     * @return the root EPackage
     */
    public EPackage getEPackage() {
        if (this.idIndex.isEmpty()) {
            return null;
        }
        Optional<SmartObject> firstObj = this.idIndex.values().stream().findFirst();
        EPackage ePackage = firstObj.get().eClass().getEPackage();
        return (EPackage) EcoreUtil.getRootContainer(ePackage);
    }

    /**
     * Export the loaded model to a given path.
     *
     * @param target        path where the exported model should be stored
     * @param exportWithIds boolean indicating if the nodeIds should be removed
     * @return whether the export was successful
     */
    public boolean exportModel(Path target, boolean exportWithIds) {
        if (this.idIndex.isEmpty()) {
            return false;
        }
        Optional<SmartObject> firstObj = this.idIndex.values().stream().findFirst();
        Resource modelResource = firstObj.get().eResource();

        Resource modelCopy = this.copyResource(modelResource, target);
        if (!exportWithIds) {
            EPackage ePackage = this.getEPackage();
            EMFExtenderUtils.unextendModel(ePackage, modelCopy);
        }

        return this.saveResource(modelCopy);
    }

    /**
     * Get the loaded resource
     *
     * @return the loaded resource
     */
    public Resource getResource() {
        return this.getResources().get(0);
    }

    /**
     * Load a model from file.
     * Overrides default SmartEMFLoader method to inject index initialization
     *
     * @param file the file to load
     * @return the loaded resource
     */
    @Override
    public Resource loadResource(File file) {
        Resource resource = super.loadResource(file);
        initializeIndices((SmartEMFResource) resource);
        return resource;
    }

    /**
     * Load a model from file.
     * Overrides default SmartEMFLoader method to inject index initialization
     *
     * @param file the file to load
     * @return the loaded resource
     */
    @Override
    public Resource loadResource(Path file) {
        Resource resource = super.loadResource(file);
        initializeIndices((SmartEMFResource) resource);
        return resource;
    }
}
