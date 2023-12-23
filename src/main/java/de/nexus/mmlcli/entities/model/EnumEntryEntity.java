package de.nexus.mmlcli.entities.model;

import de.nexus.mmlcli.serializer.EcoreIdResolver;
import org.eclipse.emf.ecore.EEnumLiteral;

import java.util.UUID;

/**
 * Dataclass for a single enum entry
 */
public class EnumEntryEntity<T> {
	private String referenceId;
	private String name;
	private boolean hasDefaultValue;
	private T defaultValue;

	public String getReferenceId() {
		return referenceId;
	}

	public String getName() {
		return name;
	}

	public boolean isHasDefaultValue() {
		return hasDefaultValue;
	}

	public T getDefaultValue() {
		return defaultValue;
	}

	private void setReferenceId(String referenceId) {
		this.referenceId = referenceId;
	}

	private void setName(String name) {
		this.name = name;
	}

	private void setHasDefaultValue(boolean hasDefaultValue) {
		this.hasDefaultValue = hasDefaultValue;
	}

	private void setDefaultValue(T defaultValue) {
		this.defaultValue = defaultValue;
	}

	@Override
	public String toString() {
		return String.format("[%s|%b]", name, hasDefaultValue);
	}

	public static EnumEntryEntity<Integer> fromEEnumLiteral(EEnumLiteral eLit, EcoreIdResolver idResolver){
		EnumEntryEntity<Integer> enumEntry = new EnumEntryEntity<>();
		UUID uuid = idResolver.resolveId(eLit);
		enumEntry.setName(eLit.getName());
		enumEntry.setReferenceId(uuid.toString());
		enumEntry.setDefaultValue(eLit.getValue());
		enumEntry.setHasDefaultValue(true);
        return enumEntry;
	}
}
