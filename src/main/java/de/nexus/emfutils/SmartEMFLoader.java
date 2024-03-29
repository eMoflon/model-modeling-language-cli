package de.nexus.emfutils;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;

public class SmartEMFLoader implements IEMFLoader {
    private final ResourceSet resourceSet;

    public SmartEMFLoader(Path workspacePath) {
        this.resourceSet = EMFLoaderUtils.getSmartEMFResourceSet(workspacePath);
    }

    @Override
    public Resource loadResource(File file) {
        return EMFLoaderUtils.loadResource(this.resourceSet, file);
    }

    @Override
    public Resource loadResource(Path file) {
        return EMFLoaderUtils.loadResource(this.resourceSet, file.toFile());
    }

    @Override
    public EPackage loadResourceAsPackage(File file) {
        return EMFLoaderUtils.loadResourceAsEPackage(this.resourceSet, file);
    }

    @Override
    public EPackage loadResourceAsPackage(Path file) {
        return EMFLoaderUtils.loadResourceAsEPackage(this.resourceSet, file.toFile());
    }

    @Override
    public EPackage.Registry getPackageRegistry() {
        return this.resourceSet.getPackageRegistry();
    }

    @Override
    public Resource createNewResource(String path) {
        URI targetURI = URI.createFileURI(path);
        return this.resourceSet.createResource(targetURI);
    }

    @Override
    public Resource createNewResource(Path path) {
        URI targetURI = URI.createFileURI(path.toAbsolutePath().toString());
        return this.resourceSet.createResource(targetURI);
    }

    @Override
    public Resource createNewResource(File file) {
        URI targetURI = URI.createFileURI(file.getAbsolutePath());
        return this.resourceSet.createResource(targetURI);
    }

    @Override
    public Resource createNewResource(URI path) {
        return this.resourceSet.createResource(path);
    }

    @Override
    public boolean saveResource(Resource resource) {
        try {
            resource.save(Collections.EMPTY_MAP);
            return true;
        } catch (IOException e) {
            System.err.println("An error occured while trying to save resource: " + resource.getURI().toString());
            return false;
        }
    }

    @Override
    public Resource copyResource(Resource resource, String path) {
        Resource newResource = createNewResource(path);
        newResource.getContents().addAll(copyAll(resource.getContents()));
        return newResource;
    }

    @Override
    public Resource copyResource(Resource resource, Path path) {
        Resource newResource = createNewResource(path);
        newResource.getContents().addAll(copyAll(resource.getContents()));
        return newResource;
    }

    @Override
    public Resource copyResource(Resource resource, File file) {
        Resource newResource = createNewResource(file);
        newResource.getContents().addAll(copyAll(resource.getContents()));
        return newResource;
    }

    @Override
    public Resource copyResource(Resource resource, URI path) {
        Resource newResource = createNewResource(path);
        newResource.getContents().addAll(copyAll(resource.getContents()));
        return newResource;
    }

    @Override
    public Resource copyResource(EPackage ePackage, String path) {
        Resource resource = createNewResource(path);
        resource.getContents().add(ePackage);
        return resource;
    }

    @Override
    public Resource copyResource(EPackage ePackage, Path path) {
        Resource resource = createNewResource(path);
        resource.getContents().add(ePackage);
        return resource;
    }

    @Override
    public Resource copyResource(EPackage ePackage, File file) {
        Resource resource = createNewResource(file);
        resource.getContents().add(ePackage);
        return resource;
    }

    @Override
    public Resource copyResource(EPackage ePackage, URI path) {
        Resource resource = createNewResource(path);
        resource.getContents().add(ePackage);
        return resource;
    }

    @Override
    public EList<Resource> getResources() {
        return this.resourceSet.getResources();
    }

    protected ResourceSet getResourceSet() {
        return this.resourceSet;
    }

    private static <T> Collection<T> copyAll(Collection<? extends T> eObjects) {
        SmartEMFCopier copier = new SmartEMFCopier();
        Collection<T> result = copier.copyAll(eObjects);
        copier.copyReferences();
        return result;
    }
}
