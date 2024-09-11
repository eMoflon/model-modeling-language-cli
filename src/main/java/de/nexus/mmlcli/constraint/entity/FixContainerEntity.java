package de.nexus.mmlcli.constraint.entity;

import java.util.List;
import java.util.stream.Collectors;

public class FixContainerEntity {
    private boolean isEnableContainer;
    private String fixTitle;

    private boolean isEmptyMatchFix;
    private List<IFixStatementEntity> statements;

    public boolean isEnableContainer() {
        return isEnableContainer;
    }

    public List<IFixStatementEntity> getStatements() {
        return statements;
    }

    public boolean isEmptyMatchFix() {
        return isEmptyMatchFix;
    }

    public String toJavaCode() {
        if (this.isEnableContainer) {
            return String.format("new EnablingFixContainer(\"%s\", List.of(%s), %b)", fixTitle, this.statements.stream().map(IFixStatementEntity::toJavaCode).collect(Collectors.joining(", ")), this.isEmptyMatchFix);
        } else {
            return String.format("new DisablingFixContainer(\"%s\", List.of(%s), %b)", fixTitle, this.statements.stream().map(IFixStatementEntity::toJavaCode).collect(Collectors.joining(", ")), this.isEmptyMatchFix);
        }
    }
}
