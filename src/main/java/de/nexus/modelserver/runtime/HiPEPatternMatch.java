package de.nexus.modelserver.runtime;

import hipe.engine.match.ProductionMatch;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class HiPEPatternMatch implements IMatch {
    /**
     * The name of the pattern.
     */
    private String patternName;

    protected long hash;
    protected boolean hashInit = false;

    /**
     * The mapping between parameter names and objects.
     */
    private final Map<String, Object> parameters;

    /**
     * Initializes an empty match with the given pattern name.
     *
     * @param patternName the name of the pattern
     */
    public HiPEPatternMatch(final String patternName) {
        this.patternName = patternName;
        this.parameters = new LinkedHashMap<>();
    }

    /**
     * Initializes the match as a copy of the given match.
     *
     * @param match the match to copy
     */
    public HiPEPatternMatch(final IMatch match) {
        this.patternName = match.getPatternName();
        this.parameters = new LinkedHashMap<>();
        match.getParameterNames().forEach(parameterName -> this.parameters.put(parameterName, match.get(parameterName)));
    }

    public HiPEPatternMatch(final ProductionMatch match) {
        this(match.patternName);

        for (String label : match.getLabels()) {
            put(label, match.getNode(label));
        }
    }

    @Override
    public String getPatternName() {
        return this.patternName;
    }

    @Override
    public void setPatternName(final String patternName) {
        this.patternName = patternName;
    }

    @Override
    public Object get(final String name) {
        return parameters.get(name);
    }

    public Set<Map.Entry<String, Object>> getParameters() {
        return this.parameters.entrySet();
    }

    @Override
    public void put(final String name, final Object object) {
        parameters.put(name, object);
        hashInit = false;
    }

    @Override
    public Collection<String> getParameterNames() {
        return parameters.keySet();
    }

    @Override
    public Collection<Object> getObjects() {
        return parameters.values();
    }

    @Override
    public long getHashCode() {
        if (!hashInit) {
            hash = HashUtil.collectionToHash(parameters.values());
            hashInit = true;
        }
        return hash;
    }

    @Override
    public int hashCode() {
        return (int) getHashCode();
    }

    public void setHashCode(long hashCode) {
        this.hash = hashCode;
        hashInit = true;
    }

    @Override
    public boolean equals(final Object object) {
        return object instanceof IMatch && isEqual((IMatch) object);
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("IMatch for ").append(getPatternName()).append("(").append(this.hashCode()).append(") {").append(java.lang.System.lineSeparator());

        for (final String parameterName : getParameterNames()) {
            s.append("	").append(parameterName);
            s.append(" -> ").append(get(parameterName)).append(java.lang.System.lineSeparator());
        }
        s.append("}");

        return s.toString();
    }

    public IMatch copy() {
        return new HiPEPatternMatch(this);
    }
}
