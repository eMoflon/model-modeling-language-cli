package de.nexus.modelserver;

import de.nexus.modelserver.runtime.HiPEPatternMatch;
import de.nexus.modelserver.runtime.IMatch;
import hipe.engine.match.ProductionMatch;
import hipe.engine.message.production.ProductionResult;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class PatternRegistry {
    private final HashMap<String, Pattern> patterns = new HashMap<>();

    public PatternRegistry(IModelServerConfiguration config) {
        for (Pattern pattern : config.getPattern()) {
            this.patterns.put(pattern.getName(), pattern);
        }
    }

    public Pattern getPattern(String patternName) {
        return this.patterns.get(patternName);
    }

    public void processHiPE(Map<String, ProductionResult> extractData) {
        addNewMatches(extractData);
        deleteInvalidMatches(extractData);
    }

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
