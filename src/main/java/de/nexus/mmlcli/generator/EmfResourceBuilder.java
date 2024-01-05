package de.nexus.mmlcli.generator;

import de.nexus.mmlcli.entities.instance.GeneratorInstance;
import de.nexus.mmlcli.entities.model.ModelEntity;
import de.nexus.mmlcli.entities.model.PackageEntity;
import de.nexus.mmlcli.generator.diagnostic.DocumentDiagnostic;
import de.nexus.mmlcli.generator.diagnostic.DocumentPoint;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
            ArrayList<DocumentDiagnostic> seriousDiagnostics = doc.getDiagnostics().stream().filter(x -> x.getSeverity() == 1).collect(Collectors.toCollection(ArrayList::new));
            if (!seriousDiagnostics.isEmpty()) {
                System.err.printf("[DOCUMENT SKIP] Skipping %s due to the following errors%n", doc.uri.toString());
                System.err.flush();
                seriousDiagnostics.forEach(diag -> {
                    DocumentPoint startPoint = diag.getRange().getStart();
                    System.err.printf("%s (%s) starting in line %d:%d%n", diag.getMessage(), diag.getCode(), startPoint.getLine(), startPoint.getCharacter());
                    System.err.flush();
                });
                continue;
            }
            ModelEntity model = doc.getParsedGenerator().getTypegraph();
            for (PackageEntity pckgEntity : model.getPackages()) {
                String fileName = Path.of(doc.uri).getFileName().toString().replace(".mml", "").replace(".ecore", "") + "_"
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
