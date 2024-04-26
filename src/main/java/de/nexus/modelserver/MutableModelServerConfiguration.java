package de.nexus.modelserver;

import java.util.List;

public class MutableModelServerConfiguration implements IModelServerConfiguration {
    private final String projectName;
    private String modelPath;
    private String workspacePath;
    private String networkPath;
    private final List<Pattern> pattern;
    private final List<Class<? extends AbstractConstraint>> constraintClasses;

    public MutableModelServerConfiguration(IModelServerConfiguration configuration) {
        this.projectName = configuration.getProjectName();
        this.modelPath = configuration.getModelPath();
        this.workspacePath = configuration.getWorkspacePath();
        this.networkPath = configuration.getNetworkPath();
        this.pattern = configuration.getPattern();
        this.constraintClasses = configuration.getConstraintClasses();
    }

    @Override
    public String getProjectName() {
        return this.projectName;
    }

    @Override
    public String getModelPath() {
        return this.modelPath;
    }

    @Override
    public String getWorkspacePath() {
        return this.workspacePath;
    }

    @Override
    public String getNetworkPath() {
        return networkPath;
    }

    @Override
    public List<Pattern> getPattern() {
        return this.pattern;
    }

    @Override
    public List<Class<? extends AbstractConstraint>> getConstraintClasses() {
        return this.constraintClasses;
    }

    public void setModelPath(String modelPath) {
        this.modelPath = modelPath;
    }

    public void setWorkspacePath(String workspacePath) {
        this.workspacePath = workspacePath;
    }

    public void setNetworkPath(String networkPath) {
        this.networkPath = networkPath;
    }
}
