package de.nexus.modelserver;

import hipe.engine.HiPEContentAdapter;
import hipe.engine.IHiPEEngine;
import hipe.engine.message.production.ProductionResult;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.Map;

public class ModelServer {
    private final IHiPEEngine engine;
    private final IModelServerConfiguration configuration;
    private final PatternRegistry patternRegistry;
    private final ConstraintRegistry constraintRegistry;

    public ModelServer(IModelServerConfiguration configuration, IHiPEEngine engine) {
        this.engine = engine;
        this.configuration = configuration;

        System.out.println("[ModelServer] Starting ModelServer...");

        ((IModelServerEngine) this.engine).initializeEngine();

        System.out.println("[ModelServer] Initialized engine!");


        System.out.println("[ModelServer] Loading model...");
        IndexedEMFLoader emfLoader = new IndexedEMFLoader(Path.of(configuration.getWorkspacePath()));
        emfLoader.loadResource(Path.of(configuration.getModelPath()));

        System.out.println("[ModelServer] Creating ContentAdapter...");
        new HiPEContentAdapter(emfLoader.getResources(), this.engine);

        System.out.println("[ModelServer] Loading pattern registry...");
        this.patternRegistry = new PatternRegistry(configuration);

        System.out.println("[ModelServer] Loading constraint registry...");
        this.constraintRegistry = new ConstraintRegistry(configuration);

        this.constraintRegistry.initializePatternDeclarations(this.patternRegistry);


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

    public Map<String, ProductionResult> extractData() {
        try {
            return this.engine.extractData();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void terminateEngine() {
        System.out.println("[Engine] Terminating...");

        this.engine.terminate();

        System.out.println("[Engine] Terminated!");
    }

    public PatternRegistry getPatternRegistry() {
        return patternRegistry;
    }

    public ConstraintRegistry getConstraintRegistry() {
        return constraintRegistry;
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
}
