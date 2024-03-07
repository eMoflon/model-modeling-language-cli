package de.nexus.modelserver;

import java.util.List;

public abstract class FixContainer {
    protected final List<FixStatement> statements;
    protected final String fixTitle;

    public FixContainer(String fixTitle, List<FixStatement> statements) {
        this.fixTitle = fixTitle;
        this.statements = statements;
    }

    public List<FixStatement> getStatements() {
        return statements;
    }

    public String getFixTitle() {
        return fixTitle;
    }
}
