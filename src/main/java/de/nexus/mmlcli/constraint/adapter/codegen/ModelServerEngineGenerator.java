package de.nexus.mmlcli.constraint.adapter.codegen;

public class ModelServerEngineGenerator extends TemporaryFileObject {

    private static final String CODE_TEMPLATE = """
            package de.nexus.modelserver;
                                
            import de.nexus.modelserver.IModelServerEngine;
            import %s.hipe.engine.HiPEEngine;
            import hipe.engine.HiPEOptions;
            import %s.%sPackage;
            import hipe.network.HiPENetwork;
            import org.eclipse.emf.common.util.URI;
            import org.eclipse.emf.ecore.resource.Resource;
            import org.eclipse.emf.ecore.resource.ResourceSet;
            import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
            import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
            import org.eclipse.emf.ecore.resource.Resource.Factory.Registry;
                        
            import java.io.File;
            import java.io.IOException;
            import java.util.Map;
                                
            public class ModelServerEngine extends HiPEEngine implements IModelServerEngine {
                
                public ModelServerEngine(HiPENetwork network) {
                    super(network);
                }
                
                public void initializeEngine() {
                    %sPackage.eINSTANCE.getName();
                    
                    HiPEOptions options = new HiPEOptions();
                    options.cascadingNotifications = true;
                    options.lazyInitialization = false;
                    try {
                        this.initialize(options);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                
                public static ModelServerEngine build(String networkFilePath) {
                    ResourceSet rs = new ResourceSetImpl();
                    String cp = "";
                    Registry.INSTANCE.getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
                    rs.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
                    String path = networkFilePath;
                    File file = new File(path);
                    
                    try {
                        cp = file.getCanonicalPath();
                        cp = cp.replace("%%20", " ");
                    } catch (IOException var9) {
                        IOException e1 = var9;
                        e1.printStackTrace();
                    }
                    
                    URI uri = URI.createFileURI(cp);
                    Resource r = rs.createResource(uri);
                    
                    try {
                        r.load((Map)null);
                        HiPENetwork network = (HiPENetwork) r.getContents().get(0);
                        return new ModelServerEngine(network);
                    } catch (Exception var8) {
                        throw new RuntimeException("Network file could not be loaded via " + uri);
                    }
                }
            }
            """;

    private ModelServerEngineGenerator(String sourceCode) {
        super("ModelServerEngine", sourceCode);
    }

    public static ModelServerEngineGenerator build(String projectName) {
        String capitalizedProjectName = projectName.substring(0, 1).toUpperCase() + projectName.substring(1);
        String fullSource = String.format(CODE_TEMPLATE, projectName, projectName, capitalizedProjectName, capitalizedProjectName);
        return new ModelServerEngineGenerator(fullSource);
    }


}
