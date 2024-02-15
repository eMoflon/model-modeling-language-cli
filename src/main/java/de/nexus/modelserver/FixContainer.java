package de.nexus.modelserver;

import java.util.List;

public abstract class FixContainer {
    protected final List<FixStatement> statements;

    public FixContainer(List<FixStatement> statements) {
        this.statements = statements;
    }

    public List<FixStatement> getStatements() {
        return statements;
    }
}
