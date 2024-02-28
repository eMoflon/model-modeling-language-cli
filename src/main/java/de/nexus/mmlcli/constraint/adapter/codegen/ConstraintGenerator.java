package de.nexus.mmlcli.constraint.adapter.codegen;

import de.nexus.mmlcli.constraint.entity.ConstraintAssertionEntity;
import de.nexus.mmlcli.constraint.entity.ConstraintEntity;
import de.nexus.mmlcli.constraint.entity.ConstraintPatternDeclarationEntity;

import java.util.stream.Collectors;

public class ConstraintGenerator extends TemporaryFileObject {
    private static final String CODE_TEMPLATE = """
            package de.nexus.modelserver.constraints;
                        
            import de.nexus.modelserver.AbstractConstraint;
            import de.nexus.modelserver.AttributeAssignment;
            import de.nexus.modelserver.PatternDeclaration;
            import de.nexus.modelserver.FixStatement;
            import de.nexus.modelserver.FixContainer;
            import de.nexus.modelserver.FixInfoStatement;
            import de.nexus.modelserver.FixSetStatement;
            import de.nexus.modelserver.FixCreateEdgeStatement;
            import de.nexus.modelserver.FixCreateNodeStatement;
            import de.nexus.modelserver.FixDeleteEdgeStatement;
            import de.nexus.modelserver.FixDeleteNodeStatement;
            import de.nexus.modelserver.EnablingFixContainer;
            import de.nexus.modelserver.DisablingFixContainer;
            import de.nexus.expr.*;
                        
            import java.util.List;
                        
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
        String assertionRegistrations = constraint.getAssertions().stream().map(ConstraintAssertionEntity::toJavaCode).collect(Collectors.joining("\n"));
        String patternDeclarationRegistrations = constraint.getPatternDeclarations().stream().map(ConstraintPatternDeclarationEntity::toJavaCode).collect(Collectors.joining("\n"));

        String constructor = assertionRegistrations + "\n" + patternDeclarationRegistrations;
        String fullSource = String.format(CODE_TEMPLATE, className, className, constructor, constraint.getName(), constraint.getTitle(), constraint.getDescription());
        return new ConstraintGenerator(className, fullSource);
    }
}
