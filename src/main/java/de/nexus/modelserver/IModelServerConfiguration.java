package de.nexus.modelserver;

import java.util.List;

public interface IModelServerConfiguration {
    String getProjectName();

    String getModelPath();

    String getWorkspacePath();

    List<Pattern> getPattern();

    List<Class<? extends AbstractConstraint>> getConstraintClasses();
}
