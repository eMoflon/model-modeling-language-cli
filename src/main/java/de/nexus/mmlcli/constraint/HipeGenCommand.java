package de.nexus.mmlcli.constraint;

import de.nexus.mmlcli.constraint.adapter.*;
import de.nexus.mmlcli.constraint.entity.ConstraintDocumentEntity;
import de.nexus.mmlcli.constraint.entity.EntityReferenceResolver;
import de.nexus.modelserver.ModelServer;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.emoflon.smartemf.persistence.SmartEMFResourceFactoryImpl;
import picocli.CommandLine;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.List;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "hipegen", mixinStandardHelpOptions = true, version = "v1.0.0", description = "Builds hipe network")
public class HipeGenCommand implements Callable<Integer> {
    @CommandLine.Parameters(index = "0")
    File workspacePath;

    @CommandLine.Parameters(index = "1")
    File ecorePath;

    @CommandLine.Parameters(index = "2")
    File modelPath;

    @CommandLine.Option(names = {"-f", "--file"}, paramLabel = "SERIALIZED", description = "the serialized constraint document as json file", arity = "0..1")
    File sConstraintDocPath;

    @CommandLine.Option(names = {"-p", "--package-jar"}, arity = "0..1", defaultValue = "false", description = "Package ModelServer into jar file.")
    boolean packageJar;

    @CommandLine.Option(names = {"-r", "--run-model-server"}, arity = "0..1", defaultValue = "false", negatable = true, description = "Run ModelServer after generation.")
    boolean runModelServer;
    @CommandLine.Option(names = {"-v", "--verbose"}, arity = "0..1", defaultValue = "false", description = "Print additional compiler notifications.")
    boolean verbose;

    @Override
    public Integer call() throws Exception {
        System.out.println("[ModelServerGeneration] Starting ModelServer generation...");
        double tic = System.currentTimeMillis();


        LocationRegistry locations = new LocationRegistry(this.workspacePath.toPath(), this.ecorePath.toPath(), this.modelPath.toPath());

        locations.cleanupGeneratorDirectories();

        locations.createGeneratorDirectories();

        EmfMetamodelSource metamodelSource = new EmfMetamodelSource();
        metamodelSource.load(this.ecorePath.toString());


        ResourceSet rs = new ResourceSetImpl();
        rs.getResourceFactoryRegistry().getExtensionToFactoryMap()
                .put(Resource.Factory.Registry.DEFAULT_EXTENSION, new SmartEMFResourceFactoryImpl(workspacePath.toPath().toAbsolutePath().toString()));
        try {
            rs.getURIConverter().getURIMap().put(URI.createPlatformResourceURI("/", true), URI.createFileURI(this.workspacePath.getCanonicalPath() + java.io.File.separator));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String projectName = metamodelSource.getEPackage(0).getName();
        GenModelBuilder.createGenModel(metamodelSource.getEPackage(0), locations, projectName);

        String sConstraintDoc = Files.readString(sConstraintDocPath.toPath(), StandardCharsets.UTF_8);

        ConstraintDocumentEntity cDoc = ConstraintDocumentEntity.build(sConstraintDoc);

        EntityReferenceResolver.resolve(cDoc);

        HiPEBuilder hiPEBuilder = new HiPEBuilder(metamodelSource, cDoc, locations);

        hiPEBuilder.build();

        ModelServerBuilder modelServerBuilder = new ModelServerBuilder(locations, projectName, verbose);

        modelServerBuilder.buildModelServer(packageJar);

        double toc = System.currentTimeMillis();
        System.out.println("[ModelServerGeneration] ModelServer generation completed in " + (toc - tic) / 1000.0 + " seconds.");

        if (runModelServer) {
            startModelServer(locations);
        }

        return 0;
    }


    private void startModelServer(LocationRegistry locations) {
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

            //process.waitFor();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
