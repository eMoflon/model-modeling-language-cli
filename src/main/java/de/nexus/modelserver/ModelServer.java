package de.nexus.modelserver;

import hipe.engine.HiPEContentAdapter;
import hipe.engine.IHiPEEngine;
import hipe.engine.message.production.ProductionResult;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public class ModelServer {

    private final ResourceSet resourceSet;

    private final IHiPEEngine engine;
    private final IModelServerConfiguration configuration;

    public ModelServer(IModelServerConfiguration configuration, IHiPEEngine engine) {
        this.engine = engine;
        this.configuration = configuration;

        System.out.println("[ModelServer] Starting ModelServer...");

        ((IModelServerEngine) this.engine).initializeEngine();

        System.out.println("[ModelServer] Initialized engine!");


        System.out.println("[ModelServer] Loading model...");
        this.resourceSet = new ResourceSetImpl();

        URI modelUri = URI.createFileURI(configuration.getModelPath());

        try {
            loadResource(modelUri);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        System.out.println("[ModelServer] Creating ContentAdapter...");
        new HiPEContentAdapter(resourceSet.getResources(), this.engine);

        System.out.println("""

                                                              \s
                 _____       _     _ _____                    \s
                |     |___ _| |___| |   __|___ ___ _ _ ___ ___\s
                | | | | . | . | -_| |__   | -_|  _| | | -_|  _|
                |_|_|_|___|___|___|_|_____|___|_|  \\_/|___|_| \s
                                                              \s
                """);

        try {
            ModelServerGrpc grpcServer = new ModelServerGrpc(this);
            grpcServer.start();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String, ProductionResult> extractData() throws InterruptedException {
        return this.engine.extractData();
    }

    public void terminateEngine() {
        System.out.println("[Engine] Terminating...");

        this.engine.terminate();

        System.out.println("[Engine] Terminated!");
    }

    public static void main(String[] args) {
        IModelServerConfiguration configuration;

        try {
            Class<?> configClazz = Class.forName("de.nexus.modelserver.ModelServerConfiguration");
            configuration = (IModelServerConfiguration) configClazz.getDeclaredConstructor().newInstance();

            System.out.println("[ModelServer] This is the modelserver for: " + configuration.getProjectName());
        } catch (ClassNotFoundException e) {
            System.err.println("[ModelServer] Could not find configuration!");
            throw new RuntimeException(e);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            System.err.println("[ModelServer] Could not find constructor!");
            throw new RuntimeException(e);
        }

        IHiPEEngine engine;

        try {
            Class<?> engineClazz = Class.forName("de.nexus.modelserver.ModelServerEngine");
            engine = (IHiPEEngine) engineClazz.getDeclaredConstructor().newInstance();

            System.out.println("[ModelServer] Got engine!");
            new ModelServer(configuration, engine);
        } catch (ClassNotFoundException e) {
            System.err.println("[ModelServer] Could not find engine!");
            throw new RuntimeException(e);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            System.err.println("[ModelServer] Could not find constructor!");
            throw new RuntimeException(e);
        }
    }

    public void loadResource(URI modelUri) throws Exception {
        Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
        this.resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
        this.resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(Resource.Factory.Registry.DEFAULT_EXTENSION, new XMIResourceFactoryImpl());

        Resource modelResource = this.resourceSet.getResource(modelUri, true);
        EcoreUtil.resolveAll(this.resourceSet);

        if (modelResource == null)
            throw new IOException("File did not contain a valid model.");
    }
}
