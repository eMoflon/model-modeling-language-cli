package de.nexus.mmlcli.entities.model;

import de.nexus.mmlcli.serializer.EcoreIdResolver;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;

import java.util.ArrayList;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Dataclass for a package
 */
public class PackageEntity {
    private String referenceId;
    private String name;
    private final ArrayList<AbstractClassEntity> abstractClasses = new ArrayList<>();
    private final ArrayList<EnumEntity<?>> enums = new ArrayList<>();
    private final ArrayList<PackageEntity> subPackages = new ArrayList<>();

	public String getReferenceId() {
		return referenceId;
	}

	public String getName() {
		return name;
	}

	public ArrayList<AbstractClassEntity> getAbstractClasses() {
		return abstractClasses;
	}

	public ArrayList<EnumEntity<?>> getEnums() {
		return enums;
	}

	public ArrayList<PackageEntity> getSubPackages() {
		return subPackages;
	}

    private void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    private void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        String classesString = abstractClasses.isEmpty() ? ""
                : "\n" + abstractClasses.stream().map(AbstractClassEntity::toString).collect(Collectors.joining(","))
                + "\n";
        String enumsString = enums.isEmpty() ? ""
                : "\n" + enums.stream().map(EnumEntity::toString).collect(Collectors.joining(",")) + "\n";
        String subPackagesString = subPackages.isEmpty() ? ""
                : "\n" + subPackages.stream().map(PackageEntity::toString).collect(Collectors.joining(",")) + "\n";
        return String.format("%s{%s %s %s}", name, classesString, enumsString, subPackagesString);
    }

    public static PackageEntity fromEPackage(EPackage ePackage, EcoreIdResolver idResolver) {
        PackageEntity packageEntity = new PackageEntity();
        UUID uuid = idResolver.resolveId(ePackage);
        packageEntity.setName(ePackage.getName());
        packageEntity.setReferenceId(uuid.toString());
        packageEntity.subPackages.addAll(ePackage.getESubpackages().stream().map(subPackage -> fromEPackage(subPackage, idResolver)).toList());
        packageEntity.enums.addAll(ePackage.getEClassifiers().stream().filter(classifier -> classifier instanceof EEnum).map(eenum -> EnumEntity.fromEEnum((EEnum) eenum, idResolver)).toList());
        packageEntity.abstractClasses.addAll(ePackage.getEClassifiers().stream().filter(classifier -> classifier instanceof EClass).map(eclass -> AbstractClassEntity.fromEClass((EClass) eclass, idResolver)).toList());
        return packageEntity;
    }
}
