package de.nexus.mmlcli.constraint.adapter;

import de.nexus.emfutils.SmartEMFLoader;
import de.nexus.mmlcli.constraint.entity.PatternNodeEntity;
import de.nexus.expr.AttributePrimaryExpressionEntity;
import org.eclipse.emf.ecore.*;
import org.eclipse.emf.ecore.resource.Resource;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class EmfMetamodelSource extends SmartEMFLoader {
    private final Map<String, EClass> classMapping = new HashMap<>();

    public EmfMetamodelSource(Path workspacePath) {
        super(workspacePath);
    }

    @Override
    public EPackage loadResourceAsPackage(File file) {
        EPackage ePackage = super.loadResourceAsPackage(file);
        loadMetaModelClasses(ePackage, "");
        return ePackage;
    }

    @Override
    public EPackage loadResourceAsPackage(Path file) {
        return this.loadResourceAsPackage(file.toFile());
    }

    private void loadMetaModelClasses(final EPackage ePackage, final String pathPrefix) {
        String newPathPrefix = pathPrefix;
        if (!newPathPrefix.isEmpty()) {
            newPathPrefix = newPathPrefix + ".";
        }
        newPathPrefix = newPathPrefix + ePackage.getName() + ".";
        String finalNewPathPrefix = newPathPrefix;

        ePackage.getEClassifiers().stream().filter(x -> x instanceof EClass).forEach(clazz -> this.classMapping.put(finalNewPathPrefix + clazz.getName(), (EClass) clazz));

        ePackage.getESubpackages().forEach(p -> loadMetaModelClasses(p, finalNewPathPrefix));
    }

    public EClass resolveClass(PatternNodeEntity patternNode) {
        return this.classMapping.get(patternNode.getFQName());
    }

    public EReference resolveReference(PatternNodeEntity patternNode, String name) {
        EClass clazz = this.classMapping.get(patternNode.getFQName());
        return (EReference) clazz.getEStructuralFeature(name);
    }

    public EAttribute resolveAttribute(PatternNodeEntity patternNode, String name) {
        EClass clazz = this.classMapping.get(patternNode.getFQName());
        return (EAttribute) clazz.getEStructuralFeature(name);
    }

    public EAttribute resolveAttribute(AttributePrimaryExpressionEntity primaryExpression) {
        EClass clazz = this.classMapping.get(primaryExpression.getClassName());
        return (EAttribute) clazz.getEStructuralFeature(primaryExpression.getElementName());
    }

    public EPackage getEPackage(int idx) {
        Resource resource = getResourceSet().getResources().get(idx);
        EObject rootElement = resource.getContents().get(0);
        if (rootElement instanceof EPackage ePackage) {
            return ePackage;
        }
        return null;
    }
}
