package de.nexus.mmlcli.constraint.adapter.codegen;

import de.nexus.mmlcli.constraint.entity.ConstraintEntity;

import java.util.List;
import java.util.stream.Collectors;

public class ConstraintInitializerGenerator extends TemporaryFileObject {
    private static final String CODE_TEMPLATE = """
            package de.nexus.modelserver.constraints;
              
            import de.nexus.mmlcli.constraint.entity.expr.*;
            import de.nexus.modelserver.Constraint;
              
            import java.util.List;
              
            public class ConstraintInitializer {
                public static List<Constraint> CONSTRAINTS =
                        List.of(
                                %s
                        );
              }
            """;


    private ConstraintInitializerGenerator(String sourceCode) {
        super("ConstraintInitializer", sourceCode);
    }

    public static ConstraintInitializerGenerator build(List<ConstraintEntity> constraints) {
        String constrainInitializerTemplate = "new Constraint(\"%s\", \"%s\", \"%s\", %s)";
        String exprsInitializerTemplate = "List.of(%s)";
        String constraintInitializer = constraints.stream().map(constraint -> {
            String exprsInitializer = String.format(exprsInitializerTemplate, constraint.getAssertions().stream().map(assertion -> assertion.getExpr().toJavaCode()).collect(Collectors.joining(",")));
            return String.format(constrainInitializerTemplate, constraint.getName(), constraint.getTitle(), constraint.getDescription(), exprsInitializer);
        }).collect(Collectors.joining(",\n"));

        String fullSource = String.format(CODE_TEMPLATE, constraintInitializer);
        return new ConstraintInitializerGenerator(fullSource);
    }
}
