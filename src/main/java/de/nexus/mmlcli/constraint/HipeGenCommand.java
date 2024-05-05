package de.nexus.mmlcli.constraint;

import de.nexus.emfutils.EMFExtenderResult;
import de.nexus.emfutils.EMFExtenderUtils;
import de.nexus.mmlcli.CommandUtils;
import de.nexus.mmlcli.constraint.adapter.*;
import de.nexus.mmlcli.constraint.entity.ConstraintDocumentEntity;
import de.nexus.mmlcli.constraint.entity.EntityReferenceResolver;
import de.nexus.modelserver.ModelServer;
import org.eclipse.emf.ecore.EPackage;
import picocli.CommandLine;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "hipegen", mixinStandardHelpOptions = true, version = "v1.0.0", description = "Builds hipe network")
public class HipeGenCommand implements Callable<Integer> {
    @CommandLine.Parameters(index = "0", description = "path to the working directory of the ModelServer")
    File workspacePath;

    @CommandLine.Parameters(index = "1", description = "path to the Ecore file containing the metamodel")
    File ecorePath;

    @CommandLine.Parameters(index = "2", description = "path to the XMI file containing the model")
    File modelPath;

    @CommandLine.Option(names = {"-f", "--file"}, paramLabel = "path", description = "path to a serialized constraint document", arity = "0..1")
    File sConstraintDocPath;

    @CommandLine.Option(names = {"-p", "--package-jar"}, paramLabel = "(true|false)", arity = "0..1", defaultValue = "false", description = "pack the ModelServer into a Jar file")
    boolean packageJar;

    @CommandLine.Option(names = {"-r", "--run-model-server"}, paramLabel = "(true|false)", arity = "0..1", defaultValue = "false", negatable = true, description = "start the ModelServer after generation is complete")
    boolean runModelServer;

    @CommandLine.Option(names = {"-e", "--run-model-extender"}, paramLabel = "(true|false)", arity = "0..1", defaultValue = "false", negatable = true, description = "add unique identifiers to the metamodel and model before generating the ModelServer")
    boolean runExtender;

    @CommandLine.Option(names = {"-v", "--verbose"}, paramLabel = "(true|false)", arity = "0..1", defaultValue = "false", description = "prints extended compiler output")
    boolean verbose;

    @Override
    public Integer call() {
        Optional<String> loadedConstraintDoc = CommandUtils.loadDataFromFileOrStdIn(sConstraintDocPath);
        if (loadedConstraintDoc.isEmpty()) {
            return 2;
        }

        String sConstraintDoc = loadedConstraintDoc.get();

        ConstraintDocumentEntity cDoc = ConstraintDocumentEntity.build(sConstraintDoc);

        System.out.println("[ModelServerGeneration] Starting ModelServer generation...");
        double tic = System.currentTimeMillis();

        LocationRegistry locations;

        if (runExtender) {
            System.out.println("[EMFExtender] Extending Model and Metamodel...");
            double extenderTic = System.currentTimeMillis();
            EMFExtenderResult extenderResult = EMFExtenderUtils.extendFromFileToFile(this.ecorePath, this.modelPath);
            if (!extenderResult.isSuccess()) {
                System.out.println("[EMFExtender] Failed to extend Model and Metamodel!");
                return 3;
            }
            double extenderToc = System.currentTimeMillis();
            System.out.println("[EMFExtender] Extended Model and Metamodel successfully in " + (extenderToc - extenderTic) / 1000.0 + " seconds!");
            locations = new LocationRegistry(this.workspacePath.toPath(), extenderResult.getEcoreFile().toPath(), extenderResult.getModelFile().toPath());
        } else {
            locations = new LocationRegistry(this.workspacePath.toPath(), this.ecorePath.toPath(), this.modelPath.toPath());
        }


        locations.cleanupGeneratorDirectories();

        locations.createGeneratorDirectories();

        EmfMetamodelSource metamodelSource = new EmfMetamodelSource(locations.getWorkspaceDirectoryPath());
        EPackage rootPackage = metamodelSource.loadResourceAsPackage(locations.getEcorePath());

        EntityReferenceResolver.resolve(cDoc, rootPackage);

        String projectName = metamodelSource.getEPackage(0).getName();
        GenModelBuilder.createGenModel(metamodelSource.getEPackage(0), locations, projectName);

        HiPEBuilder hiPEBuilder = new HiPEBuilder(metamodelSource, cDoc, locations);

        hiPEBuilder.build();

        ModelServerBuilder modelServerBuilder = new ModelServerBuilder(locations, projectName, cDoc, verbose);

        modelServerBuilder.buildModelServer(packageJar);

        double toc = System.currentTimeMillis();
        System.out.println("[ModelServerGeneration] ModelServer generation completed in " + (toc - tic) / 1000.0 + " seconds.");

        if (runModelServer) {
            return startModelServer(locations);
        }

        return 0;
    }


    private int startModelServer(LocationRegistry locations) {
        String separator = FileSystems.getDefault().getSeparator();
        String classpath = System.getProperty("java.class.path");
        String path = System.getProperty("java.home")
                + separator + "bin" + separator + "java";

        String updatedClasspath = String.join(File.pathSeparator, List.of(classpath, locations.getBinPath().toString()));

        try {
            ProcessBuilder processBuilder =
                    new ProcessBuilder(path, "-cp",
                            updatedClasspath,
                            ModelServer.class.getName());
            Process process = processBuilder.inheritIO().start();

            Runtime.getRuntime().addShutdownHook(new Thread(process::destroy));

            return process.waitFor();
        } catch (IOException | InterruptedException ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }
}
