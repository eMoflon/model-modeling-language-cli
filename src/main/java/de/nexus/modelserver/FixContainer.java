package de.nexus.modelserver;

import java.util.List;

public abstract class FixContainer {
    protected final List<FixStatement> statements;
    protected final boolean emptyMatchFix;
    protected final String fixTitle;

    public FixContainer(String fixTitle, List<FixStatement> statements, boolean emtpyMatchFix) {
        this.fixTitle = fixTitle;
        this.statements = statements;
        this.emptyMatchFix = emtpyMatchFix;
    }

    public List<FixStatement> getStatements() {
        return statements;
    }

    public String getFixTitle() {
        return fixTitle;
    }

    public boolean isEmptyMatchFix() {
        return emptyMatchFix;
    }
}
