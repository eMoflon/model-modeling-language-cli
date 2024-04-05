package de.nexus.modelserver;

import de.nexus.modelserver.runtime.IMatch;

public interface MatchBasedStringInterpreter {
    String interpret(IMatch match);
}
