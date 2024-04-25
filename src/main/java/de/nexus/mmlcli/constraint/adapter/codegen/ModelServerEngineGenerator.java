package de.nexus.mmlcli.constraint.adapter.codegen;

public class ModelServerEngineGenerator extends TemporaryFileObject {

    private static final String CODE_TEMPLATE = """
            package de.nexus.modelserver;
                                
            import de.nexus.modelserver.IModelServerEngine;
            import %s.hipe.engine.HiPEEngine;
            import hipe.engine.HiPEOptions;
            import %s.%sPackage;
                                
            public class ModelServerEngine extends HiPEEngine implements IModelServerEngine {
                @Override
                protected String getNetworkFilePath() {
                    return "%s";
                }
                
                public void initializeEngine() {
                    %sPackage.eINSTANCE.getName();
                    
                    HiPEOptions options = new HiPEOptions();
                    options.cascadingNotifications = true;
                    options.lazyInitialization = false;
                    try {
                        this.initialize(options);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            """;

    private ModelServerEngineGenerator(String sourceCode) {
        super("ModelServerEngine", sourceCode);
    }

    public static ModelServerEngineGenerator build(String projectName, String hipeNetworkPath) {
        String normalizedNetworkPath = hipeNetworkPath.replace("\\", "\\\\");
        String capitalizedProjectName = projectName.substring(0, 1).toUpperCase() + projectName.substring(1);
        String fullSource = String.format(CODE_TEMPLATE, projectName, projectName, capitalizedProjectName, normalizedNetworkPath, capitalizedProjectName);
        return new ModelServerEngineGenerator(fullSource);
    }


}
