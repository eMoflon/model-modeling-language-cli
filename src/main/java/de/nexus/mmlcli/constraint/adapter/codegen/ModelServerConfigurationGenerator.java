package de.nexus.mmlcli.constraint.adapter.codegen;

public class ModelServerConfigurationGenerator extends TemporaryFileObject {

    private static final String CODE_TEMPLATE = """
            package de.nexus.modelserver;
                                
            import de.nexus.modelserver.IModelServerConfiguration;
                                
            public class ModelServerConfiguration implements IModelServerConfiguration {
                public String getProjectName(){
                    return "%s";
                }
                
                public String getModelPath(){
                    return "%s";
                }
            }
            """;

    private ModelServerConfigurationGenerator(String sourceCode) {
        super("ModelServerConfiguration", sourceCode);
    }

    public static ModelServerConfigurationGenerator build(String projectName, String modelPath) {
        String normalizedModelPath = modelPath.replace("\\", "\\\\");
        String fullSource = String.format(CODE_TEMPLATE, projectName, normalizedModelPath);
        return new ModelServerConfigurationGenerator(fullSource);
    }


}
