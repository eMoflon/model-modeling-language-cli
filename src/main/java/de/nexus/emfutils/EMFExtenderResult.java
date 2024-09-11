package de.nexus.emfutils;

import java.io.File;

/**
 * Dataclass for the result of EMFExtender
 */
public class EMFExtenderResult {
    private final boolean success;
    private final File ecoreFile;
    private final File modelFile;

    public EMFExtenderResult(boolean success, File ecoreFile, File modelFile) {
        this.success = success;
        this.ecoreFile = ecoreFile;
        this.modelFile = modelFile;
    }

    public File getEcoreFile() {
        return ecoreFile;
    }

    public File getModelFile() {
        return modelFile;
    }

    public boolean isSuccess() {
        return success;
    }
}
