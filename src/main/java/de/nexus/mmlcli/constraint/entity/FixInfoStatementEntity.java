package de.nexus.mmlcli.constraint.entity;

public class FixInfoStatementEntity implements IFixStatementEntity {
    private final TemplateStringEntity msg;

    public FixInfoStatementEntity(TemplateStringEntity msg) {
        this.msg = msg;
    }

    public TemplateStringEntity getMsg() {
        return msg;
    }

    @Override
    public String toJavaCode() {
        String messageCode = this.msg.toJavaCode();
        return String.format("new FixInfoStatement((match) -> %s)", messageCode);
    }
}
