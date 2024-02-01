package de.nexus.mmlcli.extender;

import de.nexus.emfutils.EMFExtenderUtils;
import picocli.CommandLine;

import java.io.File;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "extender", mixinStandardHelpOptions = true, version = "v1.0.0", description = "Extends given ecore and xmi file with identifiers")
public class ExtenderCommand implements Callable<Integer> {
    @CommandLine.Parameters(index = "0")
    File ecoreFile;
    @CommandLine.Parameters(index = "1")
    File modelFile;
    @CommandLine.Option(names = {"-i", "--invert"}, arity = "0..1", defaultValue = "false", description = "Remove identifiers if present")
    private boolean inverted;

    @Override
    public Integer call() {
        if (this.inverted) {
            EMFExtenderUtils.unextendFromFileToFile(ecoreFile, modelFile);
        } else {
            EMFExtenderUtils.extendFromFileToFile(ecoreFile, modelFile);
        }
        return 0;
    }
}
