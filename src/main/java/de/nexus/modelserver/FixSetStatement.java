package de.nexus.modelserver;

public class FixSetStatement implements FixStatement {
    private final String patternNodeName;
    private final String attributeName;
    private final boolean customizationRequired;
    private final MatchBasedStringInterpreter attributeValueInterpreter;

    public FixSetStatement(String patternNodeName, String attributeName, boolean customizationRequired, MatchBasedStringInterpreter attributeValue) {
        this.patternNodeName = patternNodeName;
        this.attributeName = attributeName;
        this.customizationRequired = customizationRequired;
        this.attributeValueInterpreter = attributeValue;
    }

    public String getPatternNodeName() {
        return patternNodeName;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public boolean isCustomizationRequired() {
        return customizationRequired;
    }

    public MatchBasedStringInterpreter getAttributeValueInterpreter() {
        return attributeValueInterpreter;
    }
}
