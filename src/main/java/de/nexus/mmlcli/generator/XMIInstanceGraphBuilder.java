package de.nexus.mmlcli.generator;

import de.nexus.mmlcli.entities.instance.AttributeEntry;
import de.nexus.mmlcli.entities.instance.ObjectInstance;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import java.io.IOException;
import java.util.*;

/**
 * The XMIInstanceGraphBuilder contains all functions to generate a model instance from a ObjectInstance as an XMI file.
 */
public class XMIInstanceGraphBuilder {
    private final ArrayList<EObject> objects = new ArrayList<>();
    private final String exportPath;

    public XMIInstanceGraphBuilder(List<ObjectInstance> objInsts, String exportPath, EcoreTypeResolver typeResolver,
                                   XMIInstanceResolver instResolver) {
        objInsts.forEach(objInst -> {
            EObject obj = typeResolver.resolveObjectInstance(objInst);
            for (AttributeEntry<?> attr : objInst.getAttributes()) {
                setAttribute(obj, attr, typeResolver, instResolver);
            }
            instResolver.registerReference(objInst.getReferenceId(), objInst.getReferences());

            this.objects.add(obj);
            instResolver.store(objInst.getReferenceId(), obj);
        });

        this.exportPath = exportPath;
    }

    public static void buildXmiFile(List<XMIInstanceGraphBuilder> graphBuilderList, EcoreTypeResolver typeResolver,
                                    XMIInstanceResolver instanceResolver, ResourceSet resSet) {
        instanceResolver.resolveUnresolvedReferences(typeResolver);

        Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
        Map<String, Object> m = reg.getExtensionToFactoryMap();
        m.put(XMIResource.XMI_NS, new XMIResourceFactoryImpl());

        List<Resource> resources = new ArrayList<>();
        // create a resource
        try {
            for (XMIInstanceGraphBuilder builder : graphBuilderList) {
                Resource resource = resSet
                        .createResource(URI.createFileURI(Objects.requireNonNull(builder.exportPath)));
                /*
                 * add your EPackage as root, everything is hierarchical included in this first
                 * node
                 */
                builder.objects.forEach(obj -> resource.getContents().add(obj));
                //System.out.println("[XMIBuilder DEBUG] Resource contains: "+resource.getContents().size());
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


    private void setAttribute(EObject obj, AttributeEntry<?> attr, EcoreTypeResolver typeResolver,
                              XMIInstanceResolver instResolver) {
        EAttribute eattr = typeResolver.resolveAttribute(attr);
        if (attr.isEnumType()) {
            obj.eSet(eattr, typeResolver.resolveAttributeEnum(attr));
        } else {
            obj.eSet(eattr, EmfGraphBuilderUtils.mapVals(eattr.getEAttributeType(), attr.getValue()));
        }

    }
}
