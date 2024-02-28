package de.nexus.modelserver;

import de.nexus.emfutils.EMFExtenderUtils;
import de.nexus.emfutils.SmartEMFLoader;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.emoflon.smartemf.runtime.SmartObject;

import java.io.File;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Optional;

public class IndexedEMFLoader extends SmartEMFLoader {
    private final HashMap<Integer, SmartObject> idIndex = new HashMap<>();

    private EStructuralFeature idStrucuralFeature = null;
    private int currentId = -1;

    public IndexedEMFLoader(Path workspace) {
        super(workspace);
    }

    private void initializeIndices(Resource resource) {
        this.idIndex.clear();
        this.idStrucuralFeature = null;
        this.currentId = -1;

        for (EObject eObject : resource.getContents()) {
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

    public int getNodeId(SmartObject object) {
        return (int) object.eGet(this.idStrucuralFeature);
    }

    public SmartObject getNode(int id) {
        return this.idIndex.get(id);
    }

    @Override
    public Resource loadResource(File file) {
        Resource resource = super.loadResource(file);
        initializeIndices(resource);
        return resource;
    }

    @Override
    public Resource loadResource(Path file) {
        Resource resource = super.loadResource(file);
        initializeIndices(resource);
        return resource;
    }
}
