package de.nexus.mmlcli.serializer;

import org.eclipse.emf.ecore.ENamedElement;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EcoreIdResolver {
    private final Map<ENamedElement, UUID> elementMapping = new HashMap<>();


    public UUID resolveId(ENamedElement classifier) {
        UUID resolved = this.elementMapping.get(classifier);
        if (resolved == null) {
            resolved = UUID.randomUUID();
            this.elementMapping.put(classifier, resolved);
        }
        return resolved;
    }
}
