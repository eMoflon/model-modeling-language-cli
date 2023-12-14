package de.nexus.mmlcli.generator;

import com.google.gson.JsonSyntaxException;
import picocli.CommandLine;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "generate", mixinStandardHelpOptions = true, version = "v1.0.0", description = "Generates Ecore and XMI files - default input by stdin")
public class GeneratorCommand implements Callable<Integer> {
    @CommandLine.Option(names = {"-f", "--file"}, paramLabel = "SERIALIZED", description = "the serialized workspace as json file", arity = "0..1")
    File serializedWorkspaceFile;
    @CommandLine.Parameters(index = "0")
    String projectName;
    @CommandLine.Parameters(index = "1")
    File outputDirectory;

    @Override
    public Integer call() throws Exception {
        String serializedContent;
        if (serializedWorkspaceFile == null) {
            Scanner scanner = new Scanner(System.in);
            serializedContent = scanner.nextLine();
            scanner.close();
        } else {
            if (!serializedWorkspaceFile.exists()) {
                System.err.println("Inputfile does not exist: " + serializedWorkspaceFile.getAbsolutePath());
                return 2;
            }
            if (!serializedWorkspaceFile.canRead()) {
                System.err.println("Could not read inputfile: " + serializedWorkspaceFile.getAbsolutePath());
                return 2;
            }
            serializedContent = Files.readString(serializedWorkspaceFile.toPath(), StandardCharsets.UTF_8);
        }

        try {
            SerializedDocument[] result = Objects.requireNonNull(SerializedDocument.deserialize(serializedContent));
            EmfResourceBuilder.buildEmfResources(result, projectName, outputDirectory);
        } catch (JsonSyntaxException | NullPointerException exception) {
            System.err.println("Deserialization failed!");
            exception.printStackTrace();
            return 1;
        }
        return 0;
    }
}