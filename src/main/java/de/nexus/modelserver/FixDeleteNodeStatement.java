package de.nexus.modelserver;

public class FixDeleteNodeStatement implements FixStatement {
    private final String nodeName;

    public FixDeleteNodeStatement(String nodeAlias) {
        this.nodeName = nodeAlias;
    }

    public String getNodeName() {
        return nodeName;
    }
}
