package de.nexus.mmlcli.entities.model;

import de.nexus.mmlcli.serializer.EcoreIdResolver;
import org.eclipse.emf.ecore.EEnum;

import java.util.ArrayList;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Dataclass for a enum
 */
public class EnumEntity<T> {
    private String referenceId;
    private String name;
    private String type;
    private ArrayList<EnumEntryEntity<T>> entries;

    public String getReferenceId() {
        return referenceId;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public ArrayList<EnumEntryEntity<T>> getEntries() {
        return entries;
    }

    private void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    private void setName(String name) {
        this.name = name;
    }

    private void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return String.format("ENUM<%s,%s>{%s}", name, type, entries.isEmpty() ? ""
                : entries.stream().map(EnumEntryEntity::toString).collect(Collectors.joining(",")));
    }

    public static EnumEntity<Integer> fromEEnum(EEnum eenum, EcoreIdResolver idResolver) {
        EnumEntity<Integer> enumEntity = new EnumEntity<>();
        UUID uuid = idResolver.resolveId(eenum);
        enumEntity.setName(eenum.getName());
        enumEntity.setReferenceId(uuid.toString());
        enumEntity.setType("unknown");
        enumEntity.entries.addAll(eenum.getELiterals().stream().map(lit -> EnumEntryEntity.fromEEnumLiteral(lit, idResolver)).toList());
        return enumEntity;
    }
}