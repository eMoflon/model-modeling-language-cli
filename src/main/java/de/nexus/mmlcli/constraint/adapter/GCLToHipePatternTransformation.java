package de.nexus.mmlcli.constraint.adapter;

import de.nexus.mmlcli.constraint.entity.*;
import hipe.pattern.*;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GCLToHipePatternTransformation {
    private HiPEPatternFactory factory;

    private Map<String, HiPEPattern> name2pattern = new HashMap<>();
    private Map<PatternNodeEntity, HiPENode> node2node = new HashMap<>();
    private HiPEContainer container;
    private final ConstraintDocumentEntity cDoc;

    private GCLToHipePatternTransformation(ConstraintDocumentEntity cDoc) {
        this.cDoc = cDoc;
    }

    public static HiPEContainer transform(EmfMetamodelSource metamodelSource, ConstraintDocumentEntity cDoc) {
        GCLToHipePatternTransformation transformation = new GCLToHipePatternTransformation(cDoc);
        return transformation.transform(metamodelSource, cDoc.getPatterns());
    }

    public HiPEContainer transform(EmfMetamodelSource metamodelSource, ArrayList<PatternEntity> patternSet) {
        factory = HiPEPatternFactory.eINSTANCE;

        name2pattern = new HashMap<>();
        node2node = new HashMap<>();

        container = factory.createHiPEContainer();

        for (PatternEntity pattern : patternSet) {
            container.getPatterns().add(transform(metamodelSource, pattern));
        }

        return container;
    }

    public HiPEPattern transform(EmfMetamodelSource metamodelSource, PatternEntity pattern) {
        assert pattern != null;
        String patternName = pattern.getName().replace("-", "_");

        if (name2pattern.containsKey(patternName))
            return name2pattern.get(patternName);

        HiPEPattern hPattern = factory.createHiPEPattern();
        hPattern.setName(patternName);

        name2pattern.put(pattern.getName(), hPattern);

        for (SupportPatternInvocationEntity inv : pattern.getPac()) {
            HiPEPatternInvocation invocation = factory.createHiPEPatternInvocation();
            HiPEPattern invoked = transform(metamodelSource, inv.getPattern());
            invocation.setInvokedPattern(invoked);
            invocation.setPositive(true);
            hPattern.getPatternInvocations().add(invocation);

            inv.getBindings().forEach(binding -> {
                HiPENode srcNode = transform(metamodelSource, binding.getPatternNode1());
                HiPENode trgNode = transform(metamodelSource, binding.getPatternNode2());
                invocation.getInvocationNodeMap().put(srcNode, trgNode);
            });
        }

        for (SupportPatternInvocationEntity inv : pattern.getNac()) {
            HiPEPatternInvocation invocation = factory.createHiPEPatternInvocation();
            HiPEPattern invoked = transform(metamodelSource, inv.getPattern());
            invocation.setInvokedPattern(invoked);
            invocation.setPositive(true);
            hPattern.getPatternInvocations().add(invocation);

            inv.getBindings().forEach(binding -> {
                HiPENode srcNode = transform(metamodelSource, binding.getPatternNode1());
                HiPENode trgNode = transform(metamodelSource, binding.getPatternNode2());
                invocation.getInvocationNodeMap().put(srcNode, trgNode);
            });
        }

        for (PatternNodeEntity node : pattern.getNodes()) {
            assert node != null;
            hPattern.getNodes().add(transform(metamodelSource, node));
        }

        for (EdgeEntity edge : pattern.getEdges()) {
            hPattern.getEdges().add(transform(metamodelSource, edge));
        }

        return hPattern;
    }

    public HiPENode transform(EmfMetamodelSource metamodelSource, PatternNodeEntity node) {
        assert node != null;
        if (node2node.containsKey(node))
            return node2node.get(node);
        HiPENode hNode = factory.createHiPENode();
        container.getNodes().add(hNode);
        hNode.setName(node.getName());

        hNode.setType(metamodelSource.resolveClass(node));

        hNode.setLocal(this.cDoc.getLocalNodes().contains(node));

        node2node.put(node, hNode);

        return hNode;
    }

    public HiPEEdge transform(EmfMetamodelSource metamodelSource, EdgeEntity edge) {
        HiPEEdge hEdge = factory.createHiPEEdge();
        container.getEdges().add(hEdge);
        EClass sourceNode = metamodelSource.resolveClass(edge.getFromNode());
        EReference reference = metamodelSource.resolveReference(edge.getFromNode(), edge.getReferenceName());
        hEdge.setName(sourceNode.getName() + "_" + reference.getName());
        hEdge.setType(reference);
        hEdge.setSource(transform(metamodelSource, edge.getFromNode()));
        hEdge.setTarget(transform(metamodelSource, edge.getToNode()));
        return hEdge;
    }
}
