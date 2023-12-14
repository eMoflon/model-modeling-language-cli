package de.nexus.mmlcli.generator;

import de.nexus.mmlcli.generator.entities.instance.GeneratorInstance;
import de.nexus.mmlcli.generator.entities.model.ModelEntity;
import de.nexus.mmlcli.generator.entities.model.PackageEntity;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * The EmfResourceBuilder coordinates the export of metamodels and instance graphs.
 * Starting from the deserialized datasets of the Language Server, metamodels and
 * instance graphs are first converted into EMF representations and finally exported.
 */
public class EmfResourceBuilder {
    public static void buildEmfResources(SerializedDocument[] documents, String projectName, File basePath) {
        System.out.println("Deserialize model");
        System.out.println("==========[Building resources]==========");
        List<EcoreTypeGraphBuilder> typeBuilders = new ArrayList<>();
        List<XMIInstanceGraphBuilder> instanceBuilders = new ArrayList<>();
        EcoreTypeResolver typeResolver = new EcoreTypeResolver();
        XMIInstanceResolver instanceResolver = new XMIInstanceResolver();
        // Obtain a new resource set
        ResourceSet resSet = new ResourceSetImpl();
        Path modelsDir = Paths.get(basePath.toString(), "model");
        for (SerializedDocument doc : documents) {
            try {
                Files.createDirectories(modelsDir);
            } catch (IOException ex) {
                System.out.println("[MODEL PATH BUILDER] Could not create models directory: " + modelsDir);
            }
            ModelEntity model = doc.getParsedGenerator().getTypegraph();
            for (PackageEntity pckgEntity : model.getPackages()) {
                String fileName = Path.of(doc.uri).getFileName().toString().replace(".mml", "") + "_"
                        + pckgEntity.getName() + ".ecore";
                Path filePath = Paths.get(modelsDir.toString(), fileName);
                String packageUri = String.format("platform:/resource/%s/model/%s", projectName, fileName);
                typeBuilders.add(new EcoreTypeGraphBuilder(pckgEntity, packageUri, filePath.toString(), typeResolver));
                System.out.printf("\t- %s [EXPORTED to %s]%n", pckgEntity.getName(), filePath);
            }
        }
        EcoreTypeGraphBuilder.buildEcoreFile(typeBuilders, typeResolver, resSet);
        System.out.println("Ecore created!");

        for (SerializedDocument doc : documents) {
            try {
                Files.createDirectories(modelsDir);
            } catch (IOException ex) {
                System.out.println("[MODEL PATH BUILDER] Could not create models directory: " + modelsDir);
            }

            for (GeneratorInstance instWrapper : doc.getParsedGenerator().getInstancegraph().getSerializedInstances()) {
                String fileName = Path.of(doc.uri).getFileName().toString().replace(".mml", "") + "_"
                        + instWrapper.getInstanceName() + ".xmi";
                Path filePath = Paths.get(modelsDir.toString(), fileName);
                instanceBuilders.add(new XMIInstanceGraphBuilder(instWrapper.getInstances(), filePath.toString(), typeResolver, instanceResolver));
                System.out.printf("\t- %s [EXPORTED to %s]%n", instWrapper.getInstanceName(), filePath);
            }
        }
        XMIInstanceGraphBuilder.buildXmiFile(instanceBuilders, typeResolver, instanceResolver, resSet);
        System.out.println("XMI created!");
    }
}
