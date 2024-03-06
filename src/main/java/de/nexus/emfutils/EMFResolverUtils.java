package de.nexus.emfutils;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

public class EMFResolverUtils {
    public static EClass getEClassByQualifiedName(EPackage rootPackage, String qualifiedName) throws IllegalArgumentException {
        ArrayList<String> splittedQNameArr = new ArrayList<>(Arrays.asList(qualifiedName.split("\\.")));
        if (splittedQNameArr.size() <= 1) {
            throw new IllegalArgumentException("Invalid qualified class name structure!");
        }
        EPackage currentPackage = rootPackage;
        for (int i = 0; i < splittedQNameArr.size() - 1; i++) {
            String currentName = splittedQNameArr.get(i);

            if (!currentPackage.getName().equals(currentName)) {
                throw new IllegalArgumentException("Invalid qualified class name - could not resolve packages!");
            }

            if (i < splittedQNameArr.size() - 2) {
                String nextName = splittedQNameArr.get(i + 1);
                Optional<EPackage> newCurrentPackage = currentPackage.getESubpackages().stream().filter(x -> x.getName().equals(nextName)).findFirst();
                if (newCurrentPackage.isEmpty()) {
                    throw new IllegalArgumentException("Invalid qualified class name - could not resolve packages!");
                } else {
                    currentPackage = newCurrentPackage.get();
                }
            }
        }

        String className = splittedQNameArr.get(splittedQNameArr.size() - 1);
        return (EClass) currentPackage.getEClassifier(className);
    }
}
