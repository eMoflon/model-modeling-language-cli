package de.nexus.mmlcli.generator;

import de.nexus.mmlcli.entities.instance.ReferenceEntry;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * XMIInstanceResolver helps to resolve and link references in instance graphs. MML replaces
 * all references with unique ids. To be independent of the execution order when constructing
 * the instance graph, the TypeResolver stores all missing references. Finally, when exporting
 * the instance graph (and thus after successful complete construction), all missing references
 * are resolved.
 */
public class XMIInstanceResolver {
    private final Map<String, EObject> objects = new HashMap<>();
    private final Map<String, List<ReferenceEntry>> unresolvedReferences = new HashMap<>();

    public void store(String objId, EObject obj) {
        this.objects.put(objId, obj);
    }

    public void registerReference(String objId, List<ReferenceEntry> references) {
        this.unresolvedReferences.put(objId, references);
    }

    public void resolveUnresolvedReferences(EcoreTypeResolver typeResolver) {
        for (Map.Entry<String, List<ReferenceEntry>> entry : this.unresolvedReferences.entrySet()) {
            EObject base = this.objects.get(entry.getKey());
            for (ReferenceEntry ref : entry.getValue()) {
                EReference eref = typeResolver.resolveReference(ref);
                if (eref.isMany()) {
                    @SuppressWarnings("unchecked")
                    EList<EObject> oldVals = (EList<EObject>) base.eGet(eref);
                    for (String refId : ref.getReferencedIds()) {
                        EObject target = this.objects.get(refId);
                        if (!oldVals.contains(target)) {
                            oldVals.add(target);
                        }
                    }
                } else {
                    if (!ref.getReferencedIds().isEmpty()) {
                        String refId = ref.getReferencedIds().get(0);
                        EObject target = this.objects.get(refId);
                        base.eSet(eref, target);
                    }
                }
            }
        }
    }
}
