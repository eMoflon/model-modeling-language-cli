package de.nexus.mmlcli.constraint.adapter;

import de.nexus.mmlcli.constraint.entity.PatternNodeEntity;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.*;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.BasicExtendedMetaData;
import org.eclipse.emf.ecore.util.ExtendedMetaData;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;

import java.util.HashMap;
import java.util.Map;

public class EmfMetamodelSource {
    private final ResourceSet resourceSet;
    private final Map<String, EClass> classMapping = new HashMap<>();

    public EmfMetamodelSource() {
        // Create a resource set.
        this.resourceSet = new ResourceSetImpl();

        // Register the default resource factory -- only needed for stand-alone!
        this.resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(
                "ecore", new EcoreResourceFactoryImpl());

        // enable extended metadata
        final ExtendedMetaData extendedMetaData = new BasicExtendedMetaData(this.resourceSet.getPackageRegistry());
        this.resourceSet.getLoadOptions().put(XMLResource.OPTION_EXTENDED_META_DATA,
                extendedMetaData);

        // Register the package -- only needed for stand-alone!
        EcorePackage ecorePackage = EcorePackage.eINSTANCE;
    }

    public void load(URI uri) {
        // Demand load the resource for this file.
        Resource resource = this.resourceSet.getResource(uri, true);

        EObject rootElement = resource.getContents().get(0);
        if (rootElement instanceof EPackage ePackage) {
            loadMetaModelClasses(ePackage, "");
        }
    }

    private void loadMetaModelClasses(final EPackage ePackage, final String pathPrefix) {
        String newPathPrefix = pathPrefix;
        if (!newPathPrefix.isEmpty()) {
            newPathPrefix = newPathPrefix + ".";
        }
        newPathPrefix = newPathPrefix + ePackage.getName() + ".";
        String finalNewPathPrefix = newPathPrefix;

        ePackage.getEClassifiers().stream().filter(x -> x instanceof EClass).forEach(clazz -> {
            this.classMapping.put(finalNewPathPrefix + clazz.getName(), (EClass) clazz);
        });

        ePackage.getESubpackages().forEach(p -> loadMetaModelClasses(p, finalNewPathPrefix));
    }

    public EClass resolveClass(PatternNodeEntity patternNode) {
        return this.classMapping.get(patternNode.getFQName());
    }

    public EReference resolveReference(PatternNodeEntity patternNode, String name) {
        EClass clazz = this.classMapping.get(patternNode.getFQName());
        return (EReference) clazz.getEStructuralFeature(name);
    }

    public EAttribute resolveAttribute(PatternNodeEntity patternNode, String name) {
        EClass clazz = this.classMapping.get(patternNode.getFQName());
        return (EAttribute) clazz.getEStructuralFeature(name);
    }

    public void load(String pathName) {
        // Get the URI of the model file.
        URI fileURI = URI.createFileURI(pathName);

        this.load(fileURI);
    }


}
