package de.nexus.modelserver;

import de.nexus.modelserver.runtime.HiPEPatternMatch;
import de.nexus.modelserver.runtime.IMatch;
import hipe.engine.match.ProductionMatch;
import hipe.engine.message.production.ProductionResult;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * The PatternRegistry keeps the overall pattern match set.
 * HiPE does not return the full pattern atch set with repeated queries,
 * but just the changes in newly added and removed pattern matches.
 * The PatternRegistry keeps all matches, updating based on HiPE information.
 */
public class PatternRegistry {
    private final HashMap<String, Pattern> patterns = new HashMap<>();

    public PatternRegistry(IModelServerConfiguration config) {
        for (Pattern pattern : config.getPattern()) {
            this.patterns.put(pattern.getName(), pattern);
        }
    }

    /**
     * Get a specific Pattern
     *
     * @param patternName the requested pattern name
     * @return the Pattern
     */
    public Pattern getPattern(String patternName) {
        return this.patterns.get(patternName);
    }

    /**
     * Get all Patterns
     *
     * @return a HashMap of all Patterns
     */
    public HashMap<String, Pattern> getPatterns() {
        return patterns;
    }

    /**
     * Process the output of a HiPE query.
     * Updates internal datastructures based on newly added and removed matches
     *
     * @param extractData response of a HiPE query
     */
    public void processHiPE(Map<String, ProductionResult> extractData) {
        addNewMatches(extractData);
        deleteInvalidMatches(extractData);
    }

    /**
     * Update internal datastructures for newly created matches
     *
     * @param extractData response of a HiPE query
     */
    private void addNewMatches(Map<String, ProductionResult> extractData) {
        for (String patternName : extractData.keySet()) {
            Collection<ProductionMatch> matches = extractData.get(patternName).getNewMatches();
            for (ProductionMatch match : matches) {
                IMatch iMatch = new HiPEPatternMatch(match);
                if (iMatch.getPatternName() == null) {
                    continue;
                }
                this.patterns.get(iMatch.getPatternName()).registerNewMatch(iMatch);
            }
        }
    }

    /**
     * Update internal datastructures for removed matches
     *
     * @param extractData response of a HiPE query
     */
    private void deleteInvalidMatches(Map<String, ProductionResult> extractData) {
        for (String patternName : extractData.keySet()) {
            Collection<ProductionMatch> matches = extractData.get(patternName).getDeleteMatches();
            for (ProductionMatch match : matches) {
                IMatch iMatch = new HiPEPatternMatch(match);
                if (iMatch.getPatternName() == null) {
                    continue;
                }
                this.patterns.get(iMatch.getPatternName()).registerDeletedMatch(iMatch);
            }
        }
    }
}
