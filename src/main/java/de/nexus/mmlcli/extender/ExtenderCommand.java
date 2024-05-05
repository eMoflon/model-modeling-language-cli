package de.nexus.mmlcli.extender;

import de.nexus.emfutils.EMFExtenderResult;
import de.nexus.emfutils.EMFExtenderUtils;
import picocli.CommandLine;

import java.io.File;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "extender", mixinStandardHelpOptions = true, version = "v1.0.0", description = "Extend a metamodel and model with a unique node identifier")
public class ExtenderCommand implements Callable<Integer> {
    @CommandLine.Parameters(index = "0", description = "path to the Ecore file containing the metamodel")
    File ecoreFile;
    @CommandLine.Parameters(index = "1", description = "path to the XMI file containing the model")
    File modelFile;
    @CommandLine.Option(names = {"-i", "--invert"}, paramLabel = "(true|false)", arity = "0..1", defaultValue = "false", description = "remove identifiers if present")
    private boolean inverted;

    @Override
    public Integer call() {
        EMFExtenderResult extenderResult;
        if (this.inverted) {
            extenderResult = EMFExtenderUtils.unextendFromFileToFile(ecoreFile, modelFile);
        } else {
            extenderResult = EMFExtenderUtils.extendFromFileToFile(ecoreFile, modelFile);
        }

        if (extenderResult.isSuccess()) {
            System.out.println("Extended successfully!");
        } else {
            System.out.println("Model-Extension failed!");
        }
        return 0;
    }
}
