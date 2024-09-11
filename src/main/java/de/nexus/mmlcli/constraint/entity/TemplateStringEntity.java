package de.nexus.mmlcli.constraint.entity;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class TemplateStringEntity {
    private final ArrayList<TemplateStringElementEntity> elements = new ArrayList<>();

    public ArrayList<TemplateStringElementEntity> getElements() {
        return elements;
    }

    public String toJavaCode() {
        return this.elements.stream().map(x -> {
            if (x.isString()) {
                return "\"" + x.getMsg() + "\"";
            } else {
                return x.getData().toInterpretableJavaCode() + ".getAsString()";
            }
        }).collect(Collectors.joining("+"));
    }
}
