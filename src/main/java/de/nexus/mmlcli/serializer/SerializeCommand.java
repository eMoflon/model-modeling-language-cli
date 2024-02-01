package de.nexus.mmlcli.serializer;

import de.nexus.emfutils.EMFLoader;
import de.nexus.mmlcli.entities.model.PackageEntity;
import org.eclipse.emf.ecore.EPackage;
import picocli.CommandLine;

import java.io.File;
import java.nio.file.Files;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "serialize", mixinStandardHelpOptions = true, version = "v1.0.0", description = "Serializes a given Ecore")
public class SerializeCommand implements Callable<Integer> {
    @CommandLine.Option(names = {"-o", "--out"}, paramLabel = "SERIALIZED", description = "path where the serialized model should be stored", arity = "0..1")
    File serializedTarget;

    @CommandLine.Parameters(index = "0")
    File ecoreFile;


    @Override
    public Integer call() {
        if (!ecoreFile.exists()) {
            System.err.println("Inputfile does not exist: " + ecoreFile.getAbsolutePath());
            return 2;
        }
        if (!ecoreFile.canRead()) {
            System.err.println("Could not read inputfile: " + ecoreFile.getAbsolutePath());
            return 2;
        }

        EMFLoader emfLoader = new EMFLoader();
        EPackage ePackage = emfLoader.loadResourceAsPackage(ecoreFile);

        PackageEntity packageEntity = MmlSerializedGenerator.buildEntities(ePackage);
        String serialized = MmlSerializedGenerator.serializeEntities(packageEntity, ecoreFile.toURI());

        if (serializedTarget == null) {
            System.out.println("=$MML-CONTENT-START$=");
            System.out.println(serialized);
        } else {
            try {
                Files.writeString(serializedTarget.toPath(), serialized);
                System.out.println("Written serialized file to: " + serializedTarget.getAbsolutePath());
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Could not write serialized file: " + serializedTarget.getAbsolutePath());
                return 2;
            }
        }
        return 0;
    }
}
