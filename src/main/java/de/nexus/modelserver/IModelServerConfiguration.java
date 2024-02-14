package de.nexus.modelserver;

import java.util.List;

public interface IModelServerConfiguration {
    String getProjectName();

    String getModelPath();

    List<Pattern> getPattern();

    List<Constraint> getConstraints();
}
