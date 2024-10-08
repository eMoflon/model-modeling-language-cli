package de.nexus.modelserver;

import hipe.engine.HiPEContentAdapter;
import hipe.engine.IHiPEEngine;
import hipe.engine.message.production.ProductionResult;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.Map;

public class ModelServer {
    private final IHiPEEngine engine;
    private final IndexedEMFLoader emfLoader;
    private final IModelServerConfiguration configuration;
    private final PatternRegistry patternRegistry;
    private final ConstraintRegistry constraintRegistry;
    private final ModelVisualizer visualizer;

    private final ModelEditProcessor editProcessor;

    public ModelServer(IModelServerConfiguration configuration, IHiPEEngine engine) throws IllegalStateException {
        this.engine = engine;
        this.configuration = configuration;

        System.out.println("[ModelServer] Starting ModelServer...");

        ((IModelServerEngine) this.engine).initializeEngine();

        System.out.println("[ModelServer] Initialized engine!");


        System.out.println("[ModelServer] Loading model...");
        this.emfLoader = new IndexedEMFLoader(Path.of(configuration.getWorkspacePath()));
        this.emfLoader.loadResource(Path.of(configuration.getModelPath()));

        System.out.println("[ModelServer] Creating ModelEditProcessor...");
        this.editProcessor = new ModelEditProcessor(this.emfLoader);

        System.out.println("[ModelServer] Creating ModelVisualizer...");
        this.visualizer = new ModelVisualizer(this.emfLoader);

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

    /**
     * Query pattern set changes from HiPE
     *
     * @return HiPE pattern set changes
     */
    public Map<String, ProductionResult> extractData() {
        try {
            return this.engine.extractData();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Query pattern set changes from HiPE and update PatternRegistry
     *
     * @return boolean indicating if any updates were processes
     */
    public boolean updatePatternMatches() {
        Map<String, ProductionResult> extractedData = this.extractData();
        this.patternRegistry.processHiPE(extractedData);
        return !extractData().isEmpty();
    }

    /**
     * Update constraint evaluations for all constraints
     */
    public void updateAllConstraintEvaluations() {
        this.constraintRegistry.getConstraints().values().forEach(this::updateConstraintEvaluation);
    }

    /**
     * Update constraint evaluation for a single constraint
     *
     * @param constraint the constraint to evaluate
     */
    public void updateConstraintEvaluation(AbstractConstraint constraint) {
        constraint.evaluate(this.patternRegistry);
        constraint.computeProposals(this.emfLoader);
    }

    /**
     * Terminate the HiPE engine
     */
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

    public IndexedEMFLoader getEmfLoader() {
        return emfLoader;
    }

    public ModelEditProcessor getEditProcessor() {
        return this.editProcessor;
    }

    public IModelServerConfiguration getConfiguration() {
        return configuration;
    }

    public ModelVisualizer getVisualizer() {
        return visualizer;
    }

    public static void main(String[] args) {
        IModelServerConfiguration configuration;

        try {
            Class<?> configClazz = Class.forName("de.nexus.modelserver.ModelServerConfiguration");
            configuration = (IModelServerConfiguration) configClazz.getDeclaredConstructor().newInstance();

            System.out.println("[ModelServer] This is the modelserver for: " + configuration.getProjectName());
        } catch (ClassNotFoundException e) {
            System.err.println("[ModelServer] Could not find configuration!");
            System.exit(1);
            throw new RuntimeException(e);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            System.exit(2);
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            System.err.println("[ModelServer] Could not find constructor!");
            System.exit(3);
            throw new RuntimeException(e);
        }

        IHiPEEngine engine;

        try {
            Class<?> engineClazz = Class.forName("de.nexus.modelserver.ModelServerEngine");
            String networkPath = configuration.getNetworkPath();
            Method engineBuildMethod = engineClazz.getMethod("build", String.class);
            engine = (IHiPEEngine) engineBuildMethod.invoke(null, networkPath);

            System.out.println("[ModelServer] Got engine!");
            new ModelServer(configuration, engine);
        } catch (ClassNotFoundException e) {
            System.err.println("[ModelServer] Could not find engine!");
            e.printStackTrace();
            System.exit(4);
            throw new RuntimeException(e);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            System.exit(5);
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            System.err.println("[ModelServer] Could not find constructor!");
            e.printStackTrace();
            System.exit(6);
            throw new RuntimeException(e);
        } catch (IllegalStateException e) {
            e.printStackTrace();
            System.exit(7);
            throw new RuntimeException(e);
        } catch (RuntimeException e) {
            e.printStackTrace();
            System.exit(8);
            throw new RuntimeException(e);
        }
    }
}
