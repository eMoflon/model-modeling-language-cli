package de.nexus.emfutils;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;

import java.io.File;
import java.nio.file.Path;

public interface IEMFLoader {
    Resource loadResource(File file);

    Resource loadResource(Path file);

    EPackage loadResourceAsPackage(File file);

    EPackage loadResourceAsPackage(Path file);

    EPackage.Registry getPackageRegistry();

    Resource createNewResource(String path);

    Resource createNewResource(Path path);

    Resource createNewResource(File file);

    Resource createNewResource(URI path);

    boolean saveResource(Resource resource);

    Resource copyResource(Resource resource, String path);

    Resource copyResource(Resource resource, Path path);

    Resource copyResource(Resource resource, File file);

    Resource copyResource(Resource resource, URI path);

    Resource copyResource(EPackage ePackage, String path);

    Resource copyResource(EPackage ePackage, Path path);

    Resource copyResource(EPackage ePackage, File file);

    Resource copyResource(EPackage ePackage, URI path);

    EList<Resource> getResources();
}
