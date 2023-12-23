package de.nexus.mmlcli.entities.model;

import de.nexus.mmlcli.serializer.EcoreIdResolver;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;

import java.util.ArrayList;
import java.util.UUID;
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

    private void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    private void setName(String name) {
        this.name = name;
    }

    private void setAbstract(boolean anAbstract) {
        isAbstract = anAbstract;
    }

    private void setInterface(boolean anInterface) {
        isInterface = anInterface;
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


    public static AbstractClassEntity fromEClass(EClass clazz, EcoreIdResolver idResolver) {
        AbstractClassEntity classEntity = new AbstractClassEntity();
        UUID uuid = idResolver.resolveId(clazz);
        classEntity.setReferenceId(uuid.toString());
        classEntity.setName(clazz.getName());
        classEntity.setAbstract(clazz.isAbstract());
        classEntity.setInterface(clazz.isInterface());

        classEntity.attributes.addAll(clazz.getEStructuralFeatures().stream().filter(sFeat -> sFeat instanceof EAttribute).map(sFeat -> AttributeEntity.fromEAttribute((EAttribute) sFeat, idResolver)).toList());
        classEntity.references.addAll(clazz.getEStructuralFeatures().stream().filter(sFeat -> sFeat instanceof EReference).map(sFeat -> CReferenceEntity.fromEReference((EReference) sFeat, idResolver)).toList());

        classEntity.extendsIds.addAll(clazz.getESuperTypes().stream().map(sClazz -> idResolver.resolveId(sClazz).toString()).toList());

        return classEntity;
    }
}
