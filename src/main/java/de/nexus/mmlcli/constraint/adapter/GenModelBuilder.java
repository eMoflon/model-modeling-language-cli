package de.nexus.mmlcli.constraint.adapter;

import org.eclipse.core.resources.IProject;
import org.eclipse.emf.codegen.ecore.genmodel.GenJDKLevel;
import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.eclipse.emf.codegen.ecore.genmodel.GenModelFactory;
import org.eclipse.emf.codegen.ecore.genmodel.GenPackage;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.emoflon.smartemf.SmartEMFGenerator;
import org.emoflon.smartemf.templates.util.TemplateUtil;

import java.io.IOException;
import java.util.Collections;

public class GenModelBuilder {

    public static void createGenModel(final EPackage rootPackage, final LocationRegistry locations,
                                      final String modelName) {

        String rootExtendsInterface = "org.eclipse.emf.ecore.impl.MinimalEObjectImpl$Container";
        GenModel genModel = GenModelFactory.eINSTANCE.createGenModel();
        genModel.setComplianceLevel(GenJDKLevel.JDK170_LITERAL);
        genModel.setModelDirectory("src-gen/" + modelName);
        genModel.getForeignModel().add(new org.eclipse.core.runtime.Path(locations.getEcorePath().toAbsolutePath().toString()).lastSegment());
        genModel.setModelName(modelName);
        genModel.setRootExtendsInterface(rootExtendsInterface);
        genModel.initialize(Collections.singleton(rootPackage));


        GenPackage genPackage = genModel.getGenPackages().get(0);
        genPackage.setPrefix(modelName);

        try {
            URI genModelURI = URI.createFileURI(locations.getModelDirectoryPath().resolve("test.genmodel").toAbsolutePath().toString());
            final XMIResourceImpl genModelResource = new XMIResourceImpl(genModelURI);
            genModelResource.getDefaultSaveOptions().put(XMLResource.OPTION_ENCODING, "UTF-8");
            genModelResource.getContents().add(genModel);
            genModelResource.save(Collections.EMPTY_MAP);
        } catch (IOException e) {
            e.printStackTrace();
        }

        TemplateUtil.registerGenModel(genModel);

        IProject dummyProject = new DummyEMFProject(modelName, locations.getSrcGenPath());

        double tic = System.currentTimeMillis();

        System.out.println("[GenModelBuilder] Generate SmartEMF sources...");
        SmartEMFGenerator emfGenerator = new SmartEMFGenerator(dummyProject, rootPackage, genModel);
        emfGenerator.generateModelCode();

        double toc = System.currentTimeMillis();
        System.out.println("[GenModelBuilder] Generated SmartEMF sources in " + (toc - tic) / 1000.0 + " seconds.");
    }
}
