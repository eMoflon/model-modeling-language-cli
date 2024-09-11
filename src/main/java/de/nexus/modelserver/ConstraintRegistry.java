package de.nexus.modelserver;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public class ConstraintRegistry {
    private final HashMap<String, AbstractConstraint> constraints = new HashMap<>();

    public ConstraintRegistry(IModelServerConfiguration config) {
        for (Class<? extends AbstractConstraint> constraintClazz : config.getConstraintClasses()) {
            try {
                AbstractConstraint constraint = constraintClazz.getConstructor().newInstance();
                this.constraints.put(constraint.getName(), constraint);
            } catch (InvocationTargetException | InstantiationException | IllegalAccessException |
                     NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void initializePatternDeclarations(PatternRegistry registry) {
        this.constraints.values().forEach(constraint ->
                constraint.getPatternDeclarations().values().forEach(
                        patternDeclaration -> patternDeclaration.connectPattern(registry)
                )
        );
    }

    public AbstractConstraint getConstraint(String name) {
        return this.constraints.get(name);
    }

    public HashMap<String, AbstractConstraint> getConstraints() {
        return constraints;
    }
}
