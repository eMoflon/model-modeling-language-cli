package de.nexus.mmlcli.constraint.adapter;

import de.nexus.mmlcli.constraint.entity.ConstraintDocumentEntity;
import hipe.generator.HiPEGenerator;
import hipe.generator.HiPEGeneratorConfig;
import hipe.network.HiPENetwork;
import hipe.pattern.HiPEContainer;
import hipe.searchplan.SearchPlan;
import hipe.searchplan.simple.LocalSearchPlan;
import org.apache.commons.io.FileUtils;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class HiPEBuilder {
    private final EmfMetamodelSource metamodel;
    private final ConstraintDocumentEntity constraintDocument;

    private final Path projectDirectory;

    public HiPEBuilder(EmfMetamodelSource metamodel, ConstraintDocumentEntity constraintDocument, Path projectDirectory) {
        this.metamodel = metamodel;
        this.constraintDocument = constraintDocument;
        this.projectDirectory = projectDirectory;
    }

    public void build() {
        System.out.println("## HiPE ## Generating HiPE-Engine code...");

        double tic = System.currentTimeMillis();

        Path genDir = projectDirectory.resolve("src-gen");

        System.out.println("Cleaning old code...");
        cleanOldCode(genDir);

        System.out.println("Updating Manifest & build properties...");
        updateManifest();
        updateBuildProperties();

        try {
            Files.createDirectories(genDir);
        } catch (IOException ex) {
            System.err.println("[HiPE PATH BUILDER] Could not create hipe directory: " + genDir);
            return;
        }

        GCLToHipePatternTransformation transformation = new GCLToHipePatternTransformation();
        HiPEContainer container = transformation.transform(this.metamodel, this.constraintDocument.getPatterns());

        System.out.println("Creating search plan & generating Rete network..");
        SearchPlan searchPlan = new LocalSearchPlan(container);
        searchPlan.generateSearchPlan();
        HiPENetwork network = searchPlan.getNetwork();

        System.out.println("Generating code...");
        HiPEGeneratorConfig config = new HiPEGeneratorConfig();
        HiPEGenerator.generateCode(this.constraintDocument.getPackageName() + ".", projectDirectory.toString(), network, config);

        double toc = System.currentTimeMillis();
        System.out.println("Code generation completed in " + (toc - tic) / 1000.0 + " seconds.");

        System.out.println("Saving HiPE patterns and HiPE network..");

        saveResource(container, genDir.resolve(this.constraintDocument.getPackageName() + "/hipe/engine/hipe-patterns.xmi").toString());
        saveResource(network, genDir.resolve(this.constraintDocument.getPackageName() + "/hipe/engine/hipe-network.xmi").toString());

        System.out.println("Refreshing workspace and cleaning build ..");
        //TODO: hö?

        System.out.println("## HiPE ## --> HiPE build complete!");
    }

    private void cleanOldCode(Path genDir) {
        if (genDir.toFile().exists()) {
            System.out.println("--> Cleaning old source files in root folder: " + genDir);
            try {
                FileUtils.deleteDirectory(genDir.toFile());
            } catch (IOException e) {
                System.err.println("Folder couldn't be deleted!");
            }
        } else {
            System.out.println("--> No previously generated code found, nothing to do!");
        }
    }

    private void updateManifest() {
        // TODO: hö?
    }

    private void updateBuildProperties() {
        // TODO: hö?
    }

    public static void saveResource(EObject model, String path) {
        Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("*", new XMIResourceFactoryImpl());
        ResourceSet rs = new ResourceSetImpl();
        rs.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", new XMIResourceFactoryImpl());

        URI uri = URI.createFileURI(path);
        Resource modelResource = rs.createResource(uri);
        modelResource.getContents().add(model);

        Map<Object, Object> saveOptions = ((XMIResource) modelResource).getDefaultSaveOptions();
        saveOptions.put(XMIResource.OPTION_ENCODING, "UTF-8");
        saveOptions.put(XMIResource.OPTION_USE_XMI_TYPE, Boolean.TRUE);
        saveOptions.put(XMIResource.OPTION_SAVE_TYPE_INFORMATION, Boolean.TRUE);
        saveOptions.put(XMIResource.OPTION_SCHEMA_LOCATION_IMPLEMENTATION, Boolean.TRUE);

        try {
            ((XMIResource) modelResource).save(saveOptions);
        } catch (IOException e) {
            System.err.println("Couldn't save debug resource: \n " + e.getMessage());
        }
    }


}
