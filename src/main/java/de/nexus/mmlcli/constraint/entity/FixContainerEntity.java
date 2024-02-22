package de.nexus.mmlcli.constraint.entity;

import java.util.List;
import java.util.stream.Collectors;

public class FixContainerEntity {
    private boolean isEnableContainer;
    private List<IFixStatementEntity> statements;

    public boolean isEnableContainer() {
        return isEnableContainer;
    }

    public List<IFixStatementEntity> getStatements() {
        return statements;
    }

    public String toJavaCode() {
        if (this.isEnableContainer) {
            return String.format("new EnablingFixContainer(List.of(%s))", this.statements.stream().map(IFixStatementEntity::toJavaCode).collect(Collectors.joining(", ")));
        } else {
            return String.format("new DisablingFixContainer(List.of(%s))", this.statements.stream().map(IFixStatementEntity::toJavaCode).collect(Collectors.joining(", ")));
        }
    }
}
