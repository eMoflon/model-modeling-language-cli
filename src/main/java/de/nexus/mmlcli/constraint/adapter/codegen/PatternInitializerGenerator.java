package de.nexus.mmlcli.constraint.adapter.codegen;

import de.nexus.mmlcli.constraint.entity.PatternEntity;

import java.util.List;
import java.util.stream.Collectors;

public class PatternInitializerGenerator extends TemporaryFileObject {
    private static final String CODE_TEMPLATE = """
            package de.nexus.modelserver.patterns;
              
            import de.nexus.expr.*;
            import de.nexus.modelserver.Pattern;
              
            import java.util.List;
              
            public class PatternInitializer {
                public static List<Pattern> PATTERNS =
                        List.of(
                                %s
                        );
              }
            """;


    private PatternInitializerGenerator(String sourceCode) {
        super("PatternInitializer", sourceCode);
    }

    public static PatternInitializerGenerator build(List<PatternEntity> patterns) {
        String constrainInitializerTemplate = "new Pattern(\"%s\")";
        String constraintInitializer = patterns.stream().map(pattern -> String.format(constrainInitializerTemplate, pattern.getName())).collect(Collectors.joining(",\n"));

        String fullSource = String.format(CODE_TEMPLATE, constraintInitializer);
        return new PatternInitializerGenerator(fullSource);
    }
}
