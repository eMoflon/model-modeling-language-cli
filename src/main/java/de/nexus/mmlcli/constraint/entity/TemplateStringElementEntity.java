package de.nexus.mmlcli.constraint.entity;

import de.nexus.expr.ExpressionEntity;

public class TemplateStringElementEntity {
    private final boolean isString;
    private final String msg;
    private final ExpressionEntity data;

    public TemplateStringElementEntity(String msg) {
        this.isString = true;
        this.msg = msg;
        this.data = null;
    }

    public TemplateStringElementEntity(ExpressionEntity expr) {
        this.isString = false;
        this.msg = null;
        this.data = expr;
    }

    public boolean isString() {
        return isString;
    }

    public String getMsg() {
        return msg;
    }

    public ExpressionEntity getData() {
        return data;
    }
}
