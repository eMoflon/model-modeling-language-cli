package de.nexus.modelserver;

import de.nexus.modelserver.proto.ModelServerPatterns;

import java.util.stream.Collectors;

public class PatternProtoMapper {
    public static ModelServerPatterns.Pattern mapPattern(Pattern pattern) {
        return ModelServerPatterns.Pattern.newBuilder().setName(pattern.getName()).setNumberOfMatches(pattern.getMatches().size()).addAllMatches(pattern.getMatches().stream().map(x -> ModelServerPatterns.Match.newBuilder().addAllNodes(x.getObjects().stream().map(Object::toString).collect(Collectors.toList())).build()).collect(Collectors.toList())).build();
    }
}
