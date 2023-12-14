package de.nexus.mmlcli.generator.entities.model;

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
}
