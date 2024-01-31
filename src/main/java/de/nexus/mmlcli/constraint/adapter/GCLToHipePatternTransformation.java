package de.nexus.mmlcli.constraint.adapter;

import de.nexus.mmlcli.constraint.entity.*;
import de.nexus.mmlcli.constraint.entity.expr.*;
import hipe.pattern.*;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;

import java.util.*;

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

    private HiPEContainer transform(EmfMetamodelSource metamodelSource, ArrayList<PatternEntity> patternSet) {
        factory = HiPEPatternFactory.eINSTANCE;

        name2pattern = new HashMap<>();
        node2node = new HashMap<>();

        container = factory.createHiPEContainer();

        for (PatternEntity pattern : patternSet) {
            container.getPatterns().add(transform(metamodelSource, pattern));
        }

        return container;
    }

    private HiPEPattern transform(EmfMetamodelSource metamodelSource, PatternEntity pattern) {
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

        List<AttributeConstraintEntity> simpleAttributeConstraints = pattern.getConstraints().stream().filter(AttributeConstraintEntity::isRelationalConstraint).toList();
        List<AttributeConstraintEntity> complexAttributeConstraints = pattern.getConstraints().stream().filter(AttributeConstraintEntity::isComplexConstraint).toList();

        for (AttributeConstraintEntity attributeConstraint : simpleAttributeConstraints) {
            HiPEAttributeConstraint hiPEAttributeConstraint = transformSimpleAC(metamodelSource, attributeConstraint, hPattern);

            if (hiPEAttributeConstraint != null) {
                hPattern.getAttributeConstraints().add(hiPEAttributeConstraint);
            }
        }

        for (AttributeConstraintEntity attributeConstraint : complexAttributeConstraints) {
            HiPEAttributeConstraint hiPEAttributeConstraint = transformComplexAC(metamodelSource, attributeConstraint);

            hPattern.getAttributeConstraints().add(hiPEAttributeConstraint);
        }

        return hPattern;
    }

    private HiPENode transform(EmfMetamodelSource metamodelSource, PatternNodeEntity node) {
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

    private HiPEEdge transform(EmfMetamodelSource metamodelSource, EdgeEntity edge) {
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

    private HiPEAttributeConstraint transformSimpleAC(EmfMetamodelSource metamodelSource, AttributeConstraintEntity ace, HiPEPattern pattern) {
        RelationalConstraint relationalConstraint = factory.createRelationalConstraint();
        ExpressionEntity expr = ace.getExpr();
        if (expr instanceof BinaryExpressionEntity bexpr && bexpr.getLeft() instanceof PrimaryExpressionEntity<?> lexpr && bexpr.getRight() instanceof PrimaryExpressionEntity<?> rexpr) {
            HiPEAttribute attrLeft = transformSimple(metamodelSource, lexpr);
            HiPEAttribute attrRight = transformSimple(metamodelSource, rexpr);

            if (attrLeft == null || attrRight == null) {
                return null;
            }

            pattern.getAttributes().add(attrLeft);
            pattern.getAttributes().add(attrRight);

            relationalConstraint.setLeftAttribute(attrLeft);
            relationalConstraint.setRightAttribute(attrRight);

            relationalConstraint.setType(bexpr.getOperator().asHiPEComparatorType());

            this.container.getAttributeConstraints().add(relationalConstraint);
            return relationalConstraint;
        } else {
            throw new RuntimeException("Tried to transformSimpleAC with non-simple constraint!");
        }
    }

    private HiPEAttribute transformSimple(EmfMetamodelSource metamodelSource, PrimaryExpressionEntity<?> primaryExpression) {
        HiPEAttribute attr = factory.createHiPEAttribute();
        this.container.getAttributes().add(attr);
        if (primaryExpression.isConstantValue()) {
            attr.setValue(primaryExpression.getValue());
        } else if (primaryExpression.getType() == PrimaryExpressionEntityType.ENUM_VALUE) {
            attr.setValue(primaryExpression.getValue());
        } else if (primaryExpression.getType() == PrimaryExpressionEntityType.ATTRIBUTE) {
            PatternNodeEntity nodeEntity = this.cDoc.getId2PatternNode().get(primaryExpression.getNodeId());
            EAttribute targetAttribute = metamodelSource.resolveAttribute(primaryExpression);
            attr.setNode(this.transform(metamodelSource, nodeEntity));
            attr.setValue(targetAttribute);
            attr.setEAttribute(targetAttribute);
        }
        return attr;
    }

    private HiPEAttributeConstraint transformComplexAC(EmfMetamodelSource metamodelSource, AttributeConstraintEntity ace) {
        ComplexConstraint cConstraint = factory.createComplexConstraint();
        Collection<HiPEAttribute> attributes = new HashSet<>();

        String expression = transform2Java(metamodelSource, ace.getExpr(), attributes);
        cConstraint.getAttributes().addAll(attributes);
        cConstraint.setInitializationCode("");
        cConstraint.setPredicateCode(expression);

        container.getAttributeConstraints().add(cConstraint);
        return cConstraint;
    }

    private String transform2Java(EmfMetamodelSource metamodelSource, ExpressionEntity expr, Collection<HiPEAttribute> attributes) {
        if (expr instanceof BinaryExpressionEntity bexpr) {
            return transform2Java(metamodelSource, bexpr, attributes);
        } else if (expr instanceof PrimaryExpressionEntity<?> pexpr) {
            return transform2Java(metamodelSource, pexpr, attributes);
        } else if (expr instanceof UnaryExpressionEntity uexpr) {
            return transform2Java(metamodelSource, uexpr, attributes);
        }
        throw new RuntimeException("Unexpected ExpressionEntityType!");
    }

    private String transform2Java(EmfMetamodelSource metamodelSource, BinaryExpressionEntity binaryExpression, Collection<HiPEAttribute> attributes) {
        return switch (binaryExpression.getOperator()) {
            case EQUALS ->
                    "(" + transform2Java(metamodelSource, binaryExpression.getLeft(), attributes) + " == " + transform2Java(metamodelSource, binaryExpression.getRight(), attributes) + ")";
            case NOT_EQUALS ->
                    "(" + transform2Java(metamodelSource, binaryExpression.getLeft(), attributes) + " != " + transform2Java(metamodelSource, binaryExpression.getRight(), attributes) + ")";
            case GREATER_THAN ->
                    "(" + transform2Java(metamodelSource, binaryExpression.getLeft(), attributes) + " > " + transform2Java(metamodelSource, binaryExpression.getRight(), attributes) + ")";
            case GREATER_EQUAL_THAN ->
                    "(" + transform2Java(metamodelSource, binaryExpression.getLeft(), attributes) + " >= " + transform2Java(metamodelSource, binaryExpression.getRight(), attributes) + ")";
            case LESS_THAN ->
                    "(" + transform2Java(metamodelSource, binaryExpression.getLeft(), attributes) + " < " + transform2Java(metamodelSource, binaryExpression.getRight(), attributes) + ")";
            case LESS_EQUAL_THAN ->
                    "(" + transform2Java(metamodelSource, binaryExpression.getLeft(), attributes) + " <= " + transform2Java(metamodelSource, binaryExpression.getRight(), attributes) + ")";
            case LOGICAL_AND ->
                    "(" + transform2Java(metamodelSource, binaryExpression.getLeft(), attributes) + " && " + transform2Java(metamodelSource, binaryExpression.getRight(), attributes) + ")";
            case LOGICAL_OR ->
                    "(" + transform2Java(metamodelSource, binaryExpression.getLeft(), attributes) + " || " + transform2Java(metamodelSource, binaryExpression.getRight(), attributes) + ")";
        };
    }

    private String transform2Java(EmfMetamodelSource metamodelSource, UnaryExpressionEntity unaryExpression, Collection<HiPEAttribute> attributes) {
        return switch (unaryExpression.getOperator()) {
            case NEGATION -> "!(" + transform2Java(metamodelSource, unaryExpression.getExpr(), attributes) + ")";
        };
    }

    private String transform2Java(EmfMetamodelSource metamodelSource, PrimaryExpressionEntity<?> primaryExpression, Collection<HiPEAttribute> attributes) {
        return switch (primaryExpression.getType()) {
            case NUMBER, INTEGER, DOUBLE, BOOLEAN, ENUM_VALUE -> String.valueOf(primaryExpression.getValue());
            case STRING -> "\"" + primaryExpression.getValue() + "\"";
            case ATTRIBUTE -> {
                HiPEAttribute attr = factory.createHiPEAttribute();
                container.getAttributes().add(attr);

                PatternNodeEntity nodeEntity = this.cDoc.getId2PatternNode().get(primaryExpression.getNodeId());
                EAttribute targetAttribute = metamodelSource.resolveAttribute(primaryExpression);
                HiPENode node = this.transform(metamodelSource, nodeEntity);
                attr.setNode(node);
                attr.setValue(targetAttribute);
                attr.setEAttribute(targetAttribute);

                attributes.add(attr);

                yield node.getName() + "_" + targetAttribute.getName();
            }
        };
    }
}
