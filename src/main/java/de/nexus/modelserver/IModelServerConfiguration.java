package de.nexus.modelserver;

import java.util.List;

public interface IModelServerConfiguration {
    String getProjectName();

    String getModelPath();

    String getWorkspacePath();

    String getNetworkPath();

    List<Pattern> getPattern();

    List<Class<? extends AbstractConstraint>> getConstraintClasses();
}
