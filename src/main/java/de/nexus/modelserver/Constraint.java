package de.nexus.modelserver;

import de.nexus.mmlcli.constraint.entity.expr.ExpressionEntity;
import de.nexus.modelserver.evaltree.EvalTree;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Constraint {
    private final String name;
    private final String title;
    private final String description;
    private final List<ExpressionEntity> exprs;

    public Constraint(String name, String title, String description, List<ExpressionEntity> exprs) {
        this.name = name;
        this.title = title;
        this.description = description;
        this.exprs = exprs;
    }

    public String getName() {
        return name;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public List<ExpressionEntity> getExprs() {
        return exprs;
    }

    public List<EvalTree> evaluate(PatternRegistry patternRegistry) {
        return this.exprs.stream().map(expr -> EvalTree.build(expr, patternRegistry)).collect(Collectors.toList());
    }
}
