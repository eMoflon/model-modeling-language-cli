package de.nexus.mmlcli.generator;

import com.google.gson.JsonSyntaxException;
import de.nexus.mmlcli.CommandUtils;
import picocli.CommandLine;

import java.io.File;
import java.util.Objects;
import java.util.Optional;
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
    public Integer call() {
        Optional<String> serializedWorkspaceDoc = CommandUtils.loadDataFromFileOrStdIn(serializedWorkspaceFile);
        if (serializedWorkspaceDoc.isEmpty()) {
            return 2;
        }

        String serializedContent = serializedWorkspaceDoc.get();

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