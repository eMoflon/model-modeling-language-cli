package de.nexus.modelserver;

import de.nexus.modelserver.proto.ModelServerPatterns;

public class PatternProtoMapper {
    public static ModelServerPatterns.Pattern mapPattern(Pattern pattern) {
        return ModelServerPatterns.Pattern.newBuilder().setName(pattern.getName()).setNumberOfMatches(pattern.getMatches().size()).build();
    }
}
