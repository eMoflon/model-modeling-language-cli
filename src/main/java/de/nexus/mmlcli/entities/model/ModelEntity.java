package de.nexus.mmlcli.entities.model;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Dataclass for a complete metamodel
 */
public class ModelEntity {
    private final ArrayList<PackageEntity> packages = new ArrayList<>();

    public ArrayList<PackageEntity> getPackages() {
        return packages;
    }

    @Override
    public String toString() {
        return packages.isEmpty() ? ""
                : "\n" + packages.stream().map(PackageEntity::toString).collect(Collectors.joining(",")) + "\n";
    }

    public static ModelEntity fromPackageEntity(PackageEntity pckgEntity) {
        ModelEntity model = new ModelEntity();
        model.packages.add(pckgEntity);
        return model;
    }
}
