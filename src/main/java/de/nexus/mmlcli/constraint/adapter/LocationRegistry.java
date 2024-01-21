package de.nexus.mmlcli.constraint.adapter;

import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class LocationRegistry {
    private final Path workspaceDirectoryPath;
    private final Path modelDirectoryPath;
    private final Path ecorePath;
    private final Path modelPath;
    private final Path srcGenPath;
    private final Path binPath;

    public LocationRegistry(Path workspaceDirectoryPath, Path ecorePath, Path modelPath) {
        this.workspaceDirectoryPath = workspaceDirectoryPath;
        this.modelDirectoryPath = this.workspaceDirectoryPath.resolve("model");

        if (!this.modelDirectoryPath.getParent().equals(this.workspaceDirectoryPath)) {
            throw new IllegalArgumentException("Model directory must be located inside model directory");
        }

        this.ecorePath = ecorePath;
        this.modelPath = modelPath;

        if (!this.ecorePath.getParent().equals(this.modelDirectoryPath)) {
            throw new IllegalArgumentException("Ecore file must be located inside model directory");
        }

        if (!this.modelPath.getParent().equals(this.modelDirectoryPath)) {
            throw new IllegalArgumentException("Model XMI file must be located inside model directory");
        }

        this.srcGenPath = this.workspaceDirectoryPath.resolve("src-gen");
        this.binPath = this.workspaceDirectoryPath.resolve("bin");
    }

    public void createGeneratorDirectories() {
        try {
            Files.createDirectories(this.srcGenPath);
        } catch (IOException ex) {
            System.err.println("[LocationRegistry] Could not create src-gen directory: " + this.srcGenPath);
            return;
        }

        try {
            Files.createDirectories(this.binPath);
        } catch (IOException ex) {
            System.err.println("[LocationRegistry] Could not create bin directory: " + this.binPath);
            return;
        }
    }

    public void cleanupGeneratorDirectories() {
        cleanOldCode(this.srcGenPath);
        cleanOldCode(this.binPath);
    }

    private void cleanOldCode(Path path) {
        if (path.toFile().exists()) {
            System.out.println("[LocationRegistry] Cleaning source files in folder: " + path);
            try {
                FileUtils.deleteDirectory(path.toFile());
            } catch (IOException e) {
                System.err.println("[LocationRegistry] Folder couldn't be deleted!");
            }
        } else {
            System.out.println("[LocationRegistry] Nothing to clean, folder does not exist: " + path);
        }
    }

    public Path getWorkspaceDirectoryPath() {
        return workspaceDirectoryPath;
    }

    public Path getModelDirectoryPath() {
        return modelDirectoryPath;
    }

    public Path getEcorePath() {
        return ecorePath;
    }

    public Path getModelPath() {
        return modelPath;
    }

    public Path getSrcGenPath() {
        return srcGenPath;
    }

    public Path getBinPath() {
        return binPath;
    }
}
