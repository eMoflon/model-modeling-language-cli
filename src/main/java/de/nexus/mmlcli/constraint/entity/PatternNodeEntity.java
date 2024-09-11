package de.nexus.mmlcli.constraint.entity;

import org.eclipse.emf.ecore.EClass;

public class PatternNodeEntity {
    private String nodeId;
    private String name;
    private String fqname;
    private boolean local;
    private volatile EClass eClass;

    public String getNodeId() {
        return nodeId;
    }

    public String getName() {
        return name;
    }

    public String getFQName() {
        return fqname;
    }

    public boolean isLocal() {
        return local;
    }

    public EClass getEClass() {
        return eClass;
    }

    public void setEClass(EClass eClass) {
        this.eClass = eClass;
    }
}
