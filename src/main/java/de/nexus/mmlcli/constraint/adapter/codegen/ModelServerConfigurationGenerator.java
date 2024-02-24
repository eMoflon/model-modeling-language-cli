package de.nexus.mmlcli.constraint.adapter.codegen;

import de.nexus.mmlcli.constraint.entity.ConstraintDocumentEntity;

import java.util.stream.Collectors;

public class ModelServerConfigurationGenerator extends TemporaryFileObject {

    private static final String CODE_TEMPLATE = """
            package de.nexus.modelserver;
                                
            import de.nexus.modelserver.IModelServerConfiguration;
            import de.nexus.modelserver.patterns.PatternInitializer;
            import de.nexus.modelserver.AbstractConstraint;
            %s
                        
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
                
                public List<Class<? extends AbstractConstraint>> getConstraintClasses(){
                    return List.of(%s);
                }
            }
            """;

    private ModelServerConfigurationGenerator(String sourceCode) {
        super("ModelServerConfiguration", sourceCode);
    }

    public static ModelServerConfigurationGenerator build(String projectName, String modelPath, ConstraintDocumentEntity cDoc) {
        String constraintImport = cDoc.getConstraints().isEmpty() ? "" : "import de.nexus.modelserver.constraints.*;";
        String normalizedModelPath = modelPath.replace("\\", "\\\\");
        String constraintClasses = cDoc.getConstraints().stream().map(x -> x.getCapitalizedName() + "Constraint.class").collect(Collectors.joining(", "));
        String fullSource = String.format(CODE_TEMPLATE, constraintImport,projectName, normalizedModelPath, constraintClasses);
        return new ModelServerConfigurationGenerator(fullSource);
    }


}
