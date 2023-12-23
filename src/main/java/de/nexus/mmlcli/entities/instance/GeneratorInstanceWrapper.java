package de.nexus.mmlcli.entities.instance;

import java.util.ArrayList;

/**
 * Dataclass for list of generators for multiple XMI files
 */
public class GeneratorInstanceWrapper {
    private final ArrayList<GeneratorInstance> serializedInstances = new ArrayList<>();

    public ArrayList<GeneratorInstance> getSerializedInstances() {
        return serializedInstances;
    }
}
