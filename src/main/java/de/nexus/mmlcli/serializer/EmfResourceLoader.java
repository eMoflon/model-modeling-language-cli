package de.nexus.mmlcli.serializer;

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

import java.io.File;

public class EmfResourceLoader {
    public static EPackage loadEmfResources(File ecoreFile) {
        System.out.println("Serialize model");
        System.out.println("==========[Loading resources]==========");

        // Create a resource set.
        ResourceSet resourceSet = new ResourceSetImpl();

        // Register the default resource factory -- only needed for stand-alone!
        resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(
                "ecore", new EcoreResourceFactoryImpl());

        // enable extended metadata
        final ExtendedMetaData extendedMetaData = new BasicExtendedMetaData(resourceSet.getPackageRegistry());
        resourceSet.getLoadOptions().put(XMLResource.OPTION_EXTENDED_META_DATA,
                extendedMetaData);

        // Register the package -- only needed for stand-alone!
        EcorePackage ecorePackage = EcorePackage.eINSTANCE;

        // Get the URI of the model file.
        URI fileURI = URI.createFileURI(ecoreFile.getAbsolutePath());

        // Demand load the resource for this file.
        Resource resource = resourceSet.getResource(fileURI, true);

        EObject eObject = resource.getContents().get(0);
        if (eObject instanceof EPackage ePackage) {
            resourceSet.getPackageRegistry().put(ePackage.getNsURI(), ePackage);
            System.out.println("[Package ]" + ePackage.getName() + " | " + ePackage.getNsURI());
            return ePackage;
        }
        throw new IllegalStateException("Could not load package from ecore!");
    }
}
