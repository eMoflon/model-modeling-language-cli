package de.nexus.mmlcli.generator;

import de.nexus.mmlcli.entities.instance.AttributeEntry;
import de.nexus.mmlcli.entities.instance.ObjectInstance;
import de.nexus.mmlcli.entities.instance.ReferenceEntry;
import de.nexus.mmlcli.entities.model.AttributeEntity;
import de.nexus.mmlcli.entities.model.CReferenceEntity;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.*;
import org.eclipse.emf.ecore.util.EcoreUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The EcoreTypeResolver helps to resolve and link references in metamodels. MML replaces
 * all references with unique ids. To be independent of the execution order when constructing
 * the metamodel, the TypeResolver tries to resolve a reference using the id and stores the
 * missing reference otherwise. When exporting the metamodel (and thus after successful complete
 * construction) all missing references are finally resolved.
 */
public class EcoreTypeResolver {
    private final Map<String, EClassifier> classifiers = new HashMap<>();
    private final Map<String, EReference> references = new HashMap<>();
    private final Map<String, EAttribute> attributes = new HashMap<>();
    private final Map<String, EPackage> packages = new HashMap<>();
    private final Map<String, EEnumLiteral> elits = new HashMap<>();
    private final Map<EReference, String> unresolvedReferenceTypes = new HashMap<>();
    private final Map<EReference, String> unresolvedReferenceOpposites = new HashMap<>();
    private final Map<EAttribute, String> unresolvedAttributeEnumTypes = new HashMap<>();
    private final Map<EAttribute, String> unresolvedAttributeEnumValues = new HashMap<>();
    private final Map<EClass, List<String>> unresolvedSupertypes = new HashMap<>();

    public void store(String classId, EClassifier classifier) {
        this.classifiers.put(classId, classifier);
    }

    public void store(String refId, EReference reference) {
        this.references.put(refId, reference);
    }

    public void store(String attrId, EAttribute attibute) {
        this.attributes.put(attrId, attibute);
    }

    public void store(String pckgId, EPackage packagee) {
        this.packages.put(pckgId, packagee);
    }

    public void store(String elitId, EEnumLiteral enumLiteral) {
        this.elits.put(elitId, enumLiteral);
    }

    public void dumpResolverStorage() {
        System.out.println("================[ERROR]================");
        System.out.println("==========[EcoreTypeResolver]==========");
        System.out.println("### Packages");
        for (String key : this.packages.keySet()) {
            System.out.printf("\t- %s%n", key);
        }
        System.out.println("### Classifiers");
        for (String key : this.classifiers.keySet()) {
            System.out.printf("\t- %s%n", key);
        }
        System.out.println("### Attributes");
        for (String key : this.attributes.keySet()) {
            System.out.printf("\t- %s%n", key);
        }
        System.out.println("### References");
        for (String key : this.references.keySet()) {
            System.out.printf("\t- %s%n", key);
        }
        System.out.println("### ELits");
        for (String key : this.elits.keySet()) {
            System.out.printf("\t- %s%n", key);
        }
        System.out.println("================[ERROR]================");
    }

    public void resolveSupertypes(EClass clazz, List<String> classIds) {
        this.unresolvedSupertypes.put(clazz, classIds);
    }

    public void resolveReference(EReference ref, CReferenceEntity refEntity) {
        String typeId = refEntity.getType();
        if (classifiers.containsKey(typeId)) {
            ref.setEType(classifiers.get(typeId));
        } else {
            unresolvedReferenceTypes.put(ref, typeId);
        }

        if (refEntity.isHasOpposite()) {
            String oppositeId = refEntity.getOpposite();
            if (classifiers.containsKey(oppositeId)) {
                ref.setEType(classifiers.get(oppositeId));
            } else {
                unresolvedReferenceOpposites.put(ref, oppositeId);
            }
        }
    }

    public void resolveAttributeEnum(EAttribute attr, AttributeEntity<String> attrEntity) {
        if (!attrEntity.isEnumType()) {
            return;
        }

        String typeId = attrEntity.getType();
        if (classifiers.containsKey(typeId)) {
            attr.setEType(classifiers.get(typeId));
        } else {
            unresolvedAttributeEnumTypes.put(attr, typeId);
        }

        if (attrEntity.isHasDefaultValue()) {
            String valueId = attrEntity.getDefaultValue();
            if (elits.containsKey(valueId)) {
                attr.setDefaultValue(elits.get(valueId).getName());
            } else {
                unresolvedAttributeEnumValues.put(attr, valueId);
            }
        }
    }

    public EObject resolveObjectInstance(ObjectInstance objInst) {
        EClass clazz = (EClass) this.classifiers.get(objInst.getReferenceTypeId());
        return EcoreUtil.create(clazz);
    }

    public EAttribute resolveAttribute(AttributeEntry<?> attr) {
        return this.attributes.get(attr.getTypeId());
    }

    public EReference resolveReference(ReferenceEntry ref) {
        return this.references.get(ref.getTypeId());
    }

    public EEnumLiteral resolveAttributeEnum(AttributeEntry<?> attr) {
        return this.elits.get(attr.getValue());
    }

    public void resolveUnresovedTypes() {
        for (Map.Entry<EReference, String> refEntry : this.unresolvedReferenceTypes.entrySet()) {
            EReference ref = refEntry.getKey();
            String typeId = refEntry.getValue();
            if (classifiers.containsKey(typeId)) {
                ref.setEType(classifiers.get(typeId));
            } else {
                dumpResolverStorage();
                throw new IllegalArgumentException("Could not resolve classId: " + typeId);
            }
        }

        for (Map.Entry<EReference, String> refEntry : this.unresolvedReferenceOpposites.entrySet()) {
            EReference ref = refEntry.getKey();
            String typeId = refEntry.getValue();
            if (references.containsKey(typeId)) {
                ref.setEOpposite(references.get(typeId));
            } else {
                dumpResolverStorage();
                throw new IllegalArgumentException("Could not resolve referenceId: " + typeId);
            }
        }

        for (Map.Entry<EAttribute, String> attrEntry : this.unresolvedAttributeEnumTypes.entrySet()) {
            EAttribute attr = attrEntry.getKey();
            String typeId = attrEntry.getValue();
            if (classifiers.containsKey(typeId)) {
                attr.setEType(classifiers.get(typeId));
            } else {
                dumpResolverStorage();
                throw new IllegalArgumentException("Could not resolve enumId: " + typeId);
            }
        }

        for (Map.Entry<EAttribute, String> attrEntry : this.unresolvedAttributeEnumValues.entrySet()) {
            EAttribute attr = attrEntry.getKey();
            String typeId = attrEntry.getValue();
            if (elits.containsKey(typeId)) {
                attr.setDefaultValue(elits.get(typeId).getName());
            } else {
                dumpResolverStorage();
                throw new IllegalArgumentException("Could not resolve enum valueId: " + typeId);
            }
        }

        for (Map.Entry<EClass, List<String>> supertypeEntry : this.unresolvedSupertypes.entrySet()) {
            EClass clazz = supertypeEntry.getKey();
            List<String> supertypeIds = supertypeEntry.getValue();
            EList<EClass> superTypes = clazz.getESuperTypes();
            for (String supertypeId : supertypeIds) {
                if (classifiers.containsKey(supertypeId)) {
                    superTypes.add((EClass) classifiers.get(supertypeId));
                } else {
                    dumpResolverStorage();
                    throw new IllegalArgumentException("Could not resolve supertype classId: " + supertypeId);
                }
            }
        }
    }
}
