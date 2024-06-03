package de.nexus.modelserver;

import de.nexus.modelserver.runtime.IMatch;

/**
 * Function interface that is used by anonymous functions
 * that are generated during the ModelServer generation.
 * Provides a context for match node access.
 */
public interface MatchBasedStringInterpreter {
    String interpret(IMatch match);
}
