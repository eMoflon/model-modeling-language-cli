package de.nexus.modelserver;

import de.nexus.modelserver.runtime.IMatch;

public class FixInfoStatement implements FixStatement {
    private final MatchBasedStringInterpreter interpreter;

    public FixInfoStatement(MatchBasedStringInterpreter interpreter) {
        this.interpreter = interpreter;
    }

    public String getMsg(IMatch match) {
        return this.interpreter.interpret(match);
    }
}
