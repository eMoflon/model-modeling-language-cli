package de.nexus.mmlcli.constraint.adapter.codegen;

import de.nexus.mmlcli.constraint.entity.ConstraintEntity;

import java.util.stream.Collectors;

public class ConstraintGenerator extends TemporaryFileObject {
    private static final String CODE_TEMPLATE = """
            package de.nexus.modelserver.constraints;
                        
            import de.nexus.modelserver.AbstractConstraint;
            import de.nexus.expr.*;
                        
            public class %s extends AbstractConstraint {
                        
                public %s() {
                    %s
                }
                        
                @Override
                public String getName() {
                    return "%s";
                }
                        
                @Override
                public String getTitle() {
                    return "%s";
                }
                        
                @Override
                public String getDescription() {
                    return "%s";
                }
            }
                        
            """;


    private ConstraintGenerator(String className, String sourceCode) {
        super(className, sourceCode);
    }

    public static ConstraintGenerator build(ConstraintEntity constraint) {
        String className = constraint.getCapitalizedName() + "Constraint";
        String assertionRegisterTemplate = "this.registerAssertion(%s);\n";
        String constructor = constraint.getAssertions().stream().map(x -> String.format(assertionRegisterTemplate, x.getExpr().toJavaCode())).collect(Collectors.joining("\n"));

        String fullSource = String.format(CODE_TEMPLATE, className, className, constructor, constraint.getName(), constraint.getTitle(), constraint.getDescription());
        return new ConstraintGenerator(className, fullSource);
    }
}
