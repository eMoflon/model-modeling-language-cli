package de.nexus.modelserver.runtime;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class EmptyMatch implements IMatch {

    public static EmptyMatch INSTANCE;

    static {
        EmptyMatch.INSTANCE = new EmptyMatch();
    }

    private EmptyMatch() {
    }

    @Override
    public String getPatternName() {
        return "EMPTY_MATCH";
    }

    @Override
    public Object get(String name) {
        return null;
    }

    @Override
    public Collection<String> getParameterNames() {
        return Collections.emptyList();
    }

    @Override
    public Collection<Object> getObjects() {
        return Collections.emptyList();
    }

    @Override
    public Set<Map.Entry<String, Object>> getParameters() {
        return Collections.emptySet();
    }

    @Override
    public long getHashCode() {
        return 0;
    }
}
