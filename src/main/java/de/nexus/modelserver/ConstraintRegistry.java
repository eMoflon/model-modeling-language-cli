package de.nexus.modelserver;

import java.util.HashMap;

public class ConstraintRegistry {
    private HashMap<String, Constraint> constraints = new HashMap<>();

    public ConstraintRegistry(IModelServerConfiguration config) {
        for (Constraint constraint : config.getConstraints()) {
            this.constraints.put(constraint.getName(), constraint);
        }
    }

    public Constraint getConstraint(String name) {
        return this.constraints.get(name);
    }

    public HashMap<String, Constraint> getConstraints() {
        return constraints;
    }
}
