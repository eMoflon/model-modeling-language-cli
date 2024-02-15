package de.nexus.mmlcli.constraint.entity;

public class FixInfoStatementEntity implements IFixStatementEntity {
    private final String msg;

    public FixInfoStatementEntity(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    @Override
    public String toJavaCode() {
        return String.format("new FixInfoStatement(\"%s\")", this.msg);
    }
}
