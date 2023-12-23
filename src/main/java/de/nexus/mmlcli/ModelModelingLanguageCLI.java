package de.nexus.mmlcli;

import de.nexus.mmlcli.generator.GeneratorCommand;
import de.nexus.mmlcli.serializer.SerializeCommand;
import picocli.CommandLine;

@CommandLine.Command(name = "mmlcli", mixinStandardHelpOptions = true, version = "v1.0.0", description = "CLI for interaction between MML, EMF and HiPE", subcommands = {GeneratorCommand.class, SerializeCommand.class})
public class ModelModelingLanguageCLI {
    public static void main(String... args) {
        int exitCode = new CommandLine(new ModelModelingLanguageCLI()).execute(args);
        System.exit(exitCode);
    }
}