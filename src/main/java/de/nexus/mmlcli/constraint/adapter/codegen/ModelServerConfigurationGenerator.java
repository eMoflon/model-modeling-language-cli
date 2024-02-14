package de.nexus.mmlcli.constraint.adapter.codegen;

public class ModelServerConfigurationGenerator extends TemporaryFileObject {

    private static final String CODE_TEMPLATE = """
            package de.nexus.modelserver;
                                
            import de.nexus.modelserver.IModelServerConfiguration;
            import de.nexus.modelserver.Constraint;
            import de.nexus.modelserver.constraints.ConstraintInitializer;
            import de.nexus.modelserver.patterns.PatternInitializer;
            
            import java.util.List;
                                
            public class ModelServerConfiguration implements IModelServerConfiguration {
                public String getProjectName(){
                    return "%s";
                }
                
                public String getModelPath(){
                    return "%s";
                }
                
                public List<Pattern> getPattern(){
                    return PatternInitializer.PATTERNS;
                }
                
                public List<Constraint> getConstraints(){
                    return ConstraintInitializer.CONSTRAINTS;
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
