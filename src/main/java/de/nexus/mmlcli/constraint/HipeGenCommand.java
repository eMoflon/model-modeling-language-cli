package de.nexus.mmlcli.constraint;

import de.nexus.mmlcli.constraint.adapter.EmfMetamodelSource;
import de.nexus.mmlcli.constraint.adapter.HiPEBuilder;
import de.nexus.mmlcli.constraint.entity.ConstraintDocumentEntity;
import de.nexus.mmlcli.constraint.entity.EntityReferenceResolver;
import picocli.CommandLine;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "hipegen", mixinStandardHelpOptions = true, version = "v1.0.0", description = "Builds hipe network")
public class HipeGenCommand implements Callable<Integer> {
    @CommandLine.Parameters(index = "0")
    File ecorePath;
    @CommandLine.Parameters(index = "1")
    File sConstraintDocPath;

    @CommandLine.Parameters(index = "2")
    File workspacePath;

    @Override
    public Integer call() throws Exception {
        EmfMetamodelSource metamodelSource = new EmfMetamodelSource();
        metamodelSource.load(ecorePath.toString());

        String sConstraintDoc = Files.readString(sConstraintDocPath.toPath(), StandardCharsets.UTF_8);

        ConstraintDocumentEntity cDoc = ConstraintDocumentEntity.build(sConstraintDoc);

        EntityReferenceResolver.resolve(cDoc);

        HiPEBuilder hiPEBuilder = new HiPEBuilder(metamodelSource, cDoc, workspacePath.toPath());

        hiPEBuilder.build();

        return 0;
    }
}
