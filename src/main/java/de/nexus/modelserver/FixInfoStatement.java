package de.nexus.modelserver;

public class FixInfoStatement implements FixStatement {
    private final String msg;

    public FixInfoStatement(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }
}
