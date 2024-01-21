package de.nexus.mmlcli.constraint.adapter;

import de.nexus.mmlcli.constraint.entity.ConstraintDocumentEntity;
import hipe.generator.HiPEGenerator;
import hipe.generator.HiPEGeneratorConfig;
import hipe.network.HiPENetwork;
import hipe.pattern.HiPEContainer;
import hipe.searchplan.SearchPlan;
import hipe.searchplan.simple.LocalSearchPlan;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import java.io.IOException;
import java.util.Map;

public class HiPEBuilder {
    private final EmfMetamodelSource metamodel;
    private final ConstraintDocumentEntity constraintDocument;
    private final LocationRegistry locationRegistry;

    public HiPEBuilder(EmfMetamodelSource metamodel, ConstraintDocumentEntity constraintDocument, LocationRegistry locationRegistry) {
        this.metamodel = metamodel;
        this.constraintDocument = constraintDocument;
        this.locationRegistry = locationRegistry;
    }

    public void build() {
        System.out.println("[HiPEBuilder] Generating HiPE-Engine code...");

        double tic = System.currentTimeMillis();

        GCLToHipePatternTransformation transformation = new GCLToHipePatternTransformation();
        HiPEContainer container = transformation.transform(this.metamodel, this.constraintDocument.getPatterns());

        System.out.println("[HiPEBuilder] Creating search plan & generating Rete network..");
        SearchPlan searchPlan = new LocalSearchPlan(container);
        searchPlan.generateSearchPlan();
        HiPENetwork network = searchPlan.getNetwork();

        System.out.println("[HiPEBuilder] Generating code...");
        HiPEGeneratorConfig config = new HiPEGeneratorConfig();
        HiPEGenerator.generateCode(this.constraintDocument.getPackageName() + ".", locationRegistry.getWorkspaceDirectoryPath().toString(), network, config);

        double toc = System.currentTimeMillis();
        System.out.println("[HiPEBuilder] Code generation completed in " + (toc - tic) / 1000.0 + " seconds.");

        System.out.println("[HiPEBuilder] Saving HiPE patterns and HiPE network..");

        saveResource(container, locationRegistry.getSrcGenPath().resolve(this.constraintDocument.getPackageName() + "/hipe/engine/hipe-patterns.xmi").toString());
        saveResource(network, locationRegistry.getSrcGenPath().resolve(this.constraintDocument.getPackageName() + "/hipe/engine/hipe-network.xmi").toString());

        System.out.println("[HiPEBuilder] --> HiPE build complete!");
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
            modelResource.save(saveOptions);
        } catch (IOException e) {
            System.err.println("[HiPEBuilder] Couldn't save debug resource: \n " + e.getMessage());
        }
    }
}
