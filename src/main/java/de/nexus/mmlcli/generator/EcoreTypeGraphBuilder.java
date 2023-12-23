package de.nexus.mmlcli.generator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import de.nexus.mmlcli.entities.model.AbstractClassEntity;
import de.nexus.mmlcli.entities.model.AttributeEntity;
import de.nexus.mmlcli.entities.model.CReferenceEntity;
import de.nexus.mmlcli.entities.model.EnumEntity;
import de.nexus.mmlcli.entities.model.EnumEntryEntity;
import de.nexus.mmlcli.entities.model.PackageEntity;

/**
 * The EcoreTypeGraphBuilder contains all functions to generate a metamodel from a PackageEntity as an Ecore file.
 */
public class EcoreTypeGraphBuilder {
	private final EPackage ePackage;
	private final String exportPath;
	private final EcoreTypeResolver resolver;

	/**
	 * Build an Ecore Package
	 * @param pckg PackageEntity
	 * @param targetUri Package URI
	 * @param exportPath Path for the Ecore export
	 * @param resolver EcoreTypeResolver
	 */
	public EcoreTypeGraphBuilder(PackageEntity pckg, String targetUri, String exportPath, EcoreTypeResolver resolver) {
		// create a new package
		this.ePackage = createPackage(pckg.getName(), pckg.getName(), targetUri);
		this.exportPath = exportPath;
		this.resolver = resolver;

		// store package in the resolver
		resolver.store(pckg.getReferenceId(), this.ePackage);

		// build subpackages
		pckg.getSubPackages().forEach(subPckg -> {
			EPackage subPackage = new EcoreTypeGraphBuilder(subPckg, targetUri, resolver).getAsSubpackage();
			this.ePackage.getESubpackages().add(subPackage);
		});
		
		// build classes
		pckg.getAbstractClasses().forEach(ab -> {
			EClass clss = createEClass(ab);
			ab.getAttributes().forEach(attr -> addAttribute(clss, attr));
			ab.getReferences().forEach(cref -> addReference(clss, cref));
		});
		
		// build enums
		pckg.getEnums().forEach(enm -> {
			EEnum enmm = createEEnum(enm);
			enm.getEntries().forEach(ee -> addEEnumLiteral(enmm, ee, enm));
		});
	}

	/**
	 * /**
	 * Build an Ecore Package
	 * <p>
	 * Does not include an export path, since this is used for subpackages
	 * 
	 * @param pckg PackageEntity
	 * @param targetUri Package URI
	 * @param resolver EcoreTypeResolver
	 */
	public EcoreTypeGraphBuilder(PackageEntity pckg, String targetUri, EcoreTypeResolver resolver) {
		this(pckg, targetUri, null, resolver);
	}

	/**
	 * Export EPackages to Ecore files
	 * 
	 * @param graphBuilderList List of EcoreTypeGraphBuilders
	 * @param resolver EcoreTypeResolver
	 * @param resSet Common resource set
	 */
	public static void buildEcoreFile(List<EcoreTypeGraphBuilder> graphBuilderList, EcoreTypeResolver resolver,
			ResourceSet resSet) {
		// resolve all unresolved types
		resolver.resolveUnresovedTypes();

		for (EcoreTypeGraphBuilder builder : graphBuilderList) {
			builder.ePackage.eClass();
		}
		Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
		Map<String, Object> m = reg.getExtensionToFactoryMap();
		m.put(EcorePackage.eNAME, new EcoreResourceFactoryImpl());

		List<Resource> resources = new ArrayList<>();
		// create a resource
		try {
			for (EcoreTypeGraphBuilder builder : graphBuilderList) {
				Resource resource = resSet
						.createResource(URI.createFileURI(Objects.requireNonNull(builder.exportPath)));
				/*
				 * add your EPackage as root, everything is hierarchical included in this first
				 * node
				 */
				resource.getContents().add(builder.ePackage);
				resources.add(resource);
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
		}

		// now save the content.
		for (Resource resource : resources) {
			try {
				resource.save(Collections.EMPTY_MAP);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public EPackage getAsSubpackage() {
		return this.ePackage;
	}

	@SuppressWarnings("unchecked")
	private void addAttribute(EClass containerClass, AttributeEntity<?> attr) {
		final EAttribute attribute = EcoreFactory.eINSTANCE.createEAttribute();
		resolver.store(attr.getReferenceId(), attribute);
		// always add to container first
		containerClass.getEStructuralFeatures().add(attribute);
		attribute.setName(attr.getName());
		attribute.setLowerBound(0);
		attribute.setUpperBound(1);

		if (attr.isEnumType()) {
			resolver.resolveAttributeEnum(attribute, (AttributeEntity<String>) attr);
		} else {
			attribute.setEType(EmfGraphBuilderUtils.mapETypes(attr.getType()));

			if (attr.isHasDefaultValue()) {
				Object val = EmfGraphBuilderUtils.mapVals(attr.getType(), attr.getDefaultValue());
				attribute.setDefaultValue(val);
			}
		}
		
		attribute.setDerived(attr.getModifiers().isDerived());
		attribute.setOrdered(attr.getModifiers().isOrdered());
		attribute.setTransient(attr.getModifiers().isTransient());
		attribute.setUnique(attr.getModifiers().isUnique());
		attribute.setUnsettable(attr.getModifiers().isUnsettable());
		attribute.setVolatile(attr.getModifiers().isVolatile());
		attribute.setChangeable(!attr.getModifiers().isReadonly());
		attribute.setID(attr.getModifiers().isId());
	}

	private void addReference(EClass containerClass, CReferenceEntity cref) {
		final EReference reference = EcoreFactory.eINSTANCE.createEReference();
		resolver.store(cref.getReferenceId(), reference);
		// always add to container first
		containerClass.getEStructuralFeatures().add(reference);
		reference.setName(cref.getName());

		resolver.resolveReference(reference, cref);

		if (cref.getMultiplicity().isLowerIsN0()) {
			reference.setLowerBound(0);
		} else if (cref.getMultiplicity().isLowerIsN()) {
			reference.setLowerBound(1);
		} else {
			reference.setLowerBound(cref.getMultiplicity().getLower());
		}

		if (cref.getMultiplicity().isHasUpperBound()) {
			if (cref.getMultiplicity().isUpperIsN0() || cref.getMultiplicity().isUpperIsN()) {
				reference.setUpperBound(-1);
			} else {
				reference.setUpperBound(cref.getMultiplicity().getUpper());
			}
		} else {
			if (cref.getMultiplicity().isLowerIsN0() || cref.getMultiplicity().isLowerIsN()) {
				reference.setUpperBound(-1);
			} else {
				reference.setUpperBound(1);
			}
		}

		reference.setDerived(cref.getModifiers().isDerived());
		reference.setChangeable(!cref.getModifiers().isReadonly());
		reference.setVolatile(cref.getModifiers().isVolatile());
		reference.setUnsettable(cref.getModifiers().isUnsettable());
		reference.setUnique(cref.getModifiers().isUnique());
		reference.setTransient(cref.getModifiers().isTransient());
		reference.setOrdered(cref.getModifiers().isOrdered());
		reference.setResolveProxies(cref.getModifiers().isResolve());
	}

	private EPackage createPackage(final String name, final String prefix, final String uri) {
		final EPackage epackage = EcoreFactory.eINSTANCE.createEPackage();
		epackage.setName(name);
		epackage.setNsPrefix(prefix);
		epackage.setNsURI(uri);
		return epackage;

	}

	private EClass createEClass(final AbstractClassEntity ace) {
		final EClass eClass = EcoreFactory.eINSTANCE.createEClass();
		resolver.store(ace.getReferenceId(), eClass);
		eClass.setName(ace.getName());
		eClass.setAbstract(ace.isAbstract());
		eClass.setInterface(ace.isInterface());
		if (!ace.getExtendsIds().isEmpty() || !ace.getImplementsIds().isEmpty()) {
			List<String> allSupertypes = Stream.concat(ace.getExtendsIds().stream(), ace.getImplementsIds().stream())
					.toList();
			resolver.resolveSupertypes(eClass, allSupertypes);
		}
		this.ePackage.getEClassifiers().add(eClass);
		return eClass;
	}

	private EEnum createEEnum(final EnumEntity<?> ee) {
		final EEnum eenum = EcoreFactory.eINSTANCE.createEEnum();
		resolver.store(ee.getReferenceId(), eenum);
		eenum.setName(ee.getName());
		this.ePackage.getEClassifiers().add(eenum);
		return eenum;
	}

	private EEnumLiteral addEEnumLiteral(final EEnum ee, final EnumEntryEntity<?> eee, final EnumEntity<?> eentity) {
		final EEnumLiteral eenumLit = EcoreFactory.eINSTANCE.createEEnumLiteral();
		resolver.store(eee.getReferenceId(), eenumLit);
		eenumLit.setName(eee.getName());
		if (eee.isHasDefaultValue()) {
			if (eentity.getType().equals("int") || eentity.getType().equals("float")
					|| eentity.getType().equals("double")) {
				int val = Double.valueOf(eee.getDefaultValue().toString()).intValue();
				eenumLit.setValue(val);
			}
		}
		ee.getELiterals().add(eenumLit);
		return eenumLit;
	}
}
