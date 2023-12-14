package de.nexus.mmlcli.generator.entities.model;

import java.util.ArrayList;
import java.util.stream.Collectors;

/***
 * Dataclass for a class-like (Abstract class, class or interface)
 */
public class AbstractClassEntity {
	private String referenceId;
	private String name;
	private boolean isAbstract;
	private boolean isInterface;
	private final ArrayList<AttributeEntity<?>> attributes = new ArrayList<>();
	private final ArrayList<CReferenceEntity> references = new ArrayList<>();
	private final ArrayList<String> extendsIds = new ArrayList<>();
	private final ArrayList<String> implementsIds = new ArrayList<>();

	public String getReferenceId() {
		return referenceId;
	}

	public String getName() {
		return name;
	}

	public boolean isAbstract() {
		return isAbstract;
	}

	public boolean isInterface() {
		return isInterface;
	}

	public ArrayList<AttributeEntity<?>> getAttributes() {
		return attributes;
	}

	public ArrayList<CReferenceEntity> getReferences() {
		return references;
	}

	public ArrayList<String> getExtendsIds() {
		return extendsIds;
	}

	public ArrayList<String> getImplementsIds() {
		return implementsIds;
	}

	@Override
	public String toString() {
		String attributeString = attributes.isEmpty() ? ""
				: "\n" + attributes.stream().map(AttributeEntity::toString).collect(Collectors.joining(",")) + "\n";
		String referenceString = references.isEmpty() ? ""
				: "\n" + references.stream().map(CReferenceEntity::toString).collect(Collectors.joining(",")) + "\n";
		return String.format("%s(isAbstract:%b|isInterface:%b||%s||%s)", name, isAbstract, isInterface, attributeString,
				referenceString);
	}
}
