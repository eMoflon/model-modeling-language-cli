package de.nexus.modelserver;

import de.nexus.modelserver.runtime.IMatch;

import java.util.HashSet;
import java.util.Set;

public class Pattern {
    private final String name;

    private final Set<IMatch> matches;

    public Pattern(String name) {
        this.name = name;
        this.matches = new HashSet<>();
    }

    public String getName() {
        return name;
    }

    public Set<IMatch> getMatches() {
        return matches;
    }

    public void registerNewMatch(IMatch match) {
        this.matches.add(match);
    }

    public void registerDeletedMatch(IMatch match) {
        this.matches.remove(match);
    }

    public boolean hasAny() {
        return !this.matches.isEmpty();
    }

    public boolean hasNone() {
        return this.matches.isEmpty();
    }

    public boolean hasExact(int num) {
        return this.matches.size() == num;
    }
}
