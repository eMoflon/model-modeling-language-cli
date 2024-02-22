package de.nexus.modelserver.evaltree;

public interface IEvalTreeNode {
    String toFormattedString(int indent);

    EvalTreeValue getValue();
}
