package de.nexus.emfutils;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.BasicExtendedMetaData;
import org.eclipse.emf.ecore.util.ExtendedMetaData;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.emoflon.smartemf.persistence.SmartEMFResourceFactoryImpl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class EMFLoaderUtils {
    public static ResourceSet getResourceSet() {
        // Create a resource set.
        ResourceSet resourceSet = new ResourceSetImpl();

        // Register the default resource factory -- only needed for stand-alone!
        resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());
        resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());

        // enable extended metadata
        final ExtendedMetaData extendedMetaData = new BasicExtendedMetaData(resourceSet.getPackageRegistry());
        resourceSet.getLoadOptions().put(XMLResource.OPTION_EXTENDED_META_DATA,
                extendedMetaData);

        // Register the package -- only needed for stand-alone!
        EcorePackage ecorePackage = EcorePackage.eINSTANCE;

        return resourceSet;
    }

    public static ResourceSet getSmartEMFResourceSet(Path workspacePath) {
        // Create a resource set.
        ResourceSet resourceSet = new ResourceSetImpl();

        // Register the default resource factory -- only needed for stand-alone!
        resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap()
                .put("ecore", new EcoreResourceFactoryImpl());
        resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap()
                .put("xmi", new SmartEMFResourceFactoryImpl(workspacePath.toAbsolutePath().toString()));
        try {
            resourceSet.getURIConverter().getURIMap().put(URI.createPlatformResourceURI("/", true), URI.createFileURI(workspacePath.toFile().getCanonicalPath() + java.io.File.separator));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // enable extended metadata
        final ExtendedMetaData extendedMetaData = new BasicExtendedMetaData(resourceSet.getPackageRegistry());
        resourceSet.getLoadOptions().put(XMLResource.OPTION_EXTENDED_META_DATA,
                extendedMetaData);

        // Register the package -- only needed for stand-alone!
        EcorePackage ecorePackage = EcorePackage.eINSTANCE;

        return resourceSet;
    }

    public static EPackage loadResourceAsEPackage(ResourceSet rSet, File file) {
        Resource resource = loadResource(rSet, file);

        EObject rootElement = resource.getContents().get(0);

        if (rootElement instanceof EPackage ePackage) {
            rSet.getPackageRegistry().put(ePackage.getNsURI(), ePackage);
            return ePackage;
        }
        return null;
    }

    public static Resource loadResource(ResourceSet rSet, File file) {
        // Get the URI of the model file.
        URI fileURI = URI.createFileURI(file.toString());

        // Demand load the resource for this file.
        return rSet.getResource(fileURI, true);
    }
}
