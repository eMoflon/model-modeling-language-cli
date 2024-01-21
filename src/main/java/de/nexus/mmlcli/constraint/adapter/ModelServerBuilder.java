package de.nexus.mmlcli.constraint.adapter;

import org.apache.commons.io.FilenameUtils;

import javax.tools.*;
import java.io.*;
import java.net.URI;
import java.net.URL;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

public class ModelServerBuilder {
    private final LocationRegistry locationRegistry;
    private final String projectName;
    private final boolean verbose;

    public ModelServerBuilder(LocationRegistry locationRegistry, String projectName, boolean verbose) {
        this.locationRegistry = locationRegistry;
        this.projectName = projectName;
        this.verbose = verbose;
    }

    public void buildModelServer(boolean generateJar) {
        double tic = System.currentTimeMillis();
        System.out.println("[ModelServerBuilder] Compiling model server...");

        compileModelServer();

        if (generateJar) {
            packageModelServer();
        }

        double toc = System.currentTimeMillis();
        System.out.println("[ModelServerBuilder] Code compilation completed in " + (toc - tic) / 1000.0 + " seconds.");
    }

    private void compileModelServer() {
        JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();

        List<String> optionList = List.of("-Xlint:unchecked", "-d", locationRegistry.getBinPath().toString());
        System.out.println("[ModelServerBuilder] Compiling with options: " + String.join(" ", optionList));
        DiagnosticCollector<JavaFileObject> collector = new DiagnosticCollector<>();
        List<Path> files = this.collectCompilableSourceCode();
        compile(javaCompiler, collector, optionList, files, projectName);
    }

    private List<Path> collectCompilableSourceCode() {
        ArrayList<Path> files = new ArrayList<>();

        try {
            Files.walkFileTree(locationRegistry.getSrcGenPath(), EnumSet.of(FileVisitOption.FOLLOW_LINKS), Integer.MAX_VALUE,
                    new SimpleFileVisitor<>() {
                        @Override
                        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                            if (FilenameUtils.getExtension(file.toString()).equals("java")) {
                                files.add(file);
                            }
                            return FileVisitResult.CONTINUE;
                        }
                    });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return files;
    }

    private void compile(JavaCompiler compiler, DiagnosticCollector<JavaFileObject> collector, Iterable<String> options, Collection<Path> files, String projectName) {
        StandardJavaFileManager fileManager = compiler
                .getStandardFileManager(collector, null, null);
        @SuppressWarnings("unchecked")
        Iterable<JavaFileObject> uncompiledClasses = (Iterable<JavaFileObject>) fileManager.getJavaFileObjectsFromPaths(files);
        ArrayList<JavaFileObject> tmp = new ArrayList<>();
        uncompiledClasses.forEach(tmp::add);
        tmp.add(new ModelServerConfigFileObject(projectName, this.locationRegistry.getModelPath().toString()));
        String hipeNetworkPath = this.locationRegistry.getSrcGenPath().resolve(projectName).resolve("hipe/engine/hipe-network.xmi").toString();
        tmp.add(new ModelServerEngineFileObject(projectName, hipeNetworkPath));

        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, collector, options, null, tmp);
        boolean result = task.call();
        if (!result || !collector.getDiagnostics().isEmpty()) {
            if (!result) {
                System.err.println("Unable to compile the source.");
            }

            StringBuilder error = new StringBuilder();
            StringBuilder others = new StringBuilder();

            for (Diagnostic<? extends JavaFileObject> diagnostic : collector.getDiagnostics()) {
                String sb = String.format("[name=%s, kind=%s, line=%d, message=%s]\n", ((JavaFileObject) ((Diagnostic<?>) diagnostic).getSource()).getName(), diagnostic.getKind(), diagnostic.getLineNumber(), diagnostic.getMessage(Locale.US));
                switch (diagnostic.getKind()) {
                    case ERROR:
                        error.append(sb);
                        break;
                    case WARNING:
                    case MANDATORY_WARNING:
                    case NOTE:
                    case OTHER: {
                        others.append(sb);
                        break;
                    }
                }
            }

            if (!error.isEmpty()) {
                System.err.println(error);
            }

            if (!others.isEmpty() && this.verbose) {
                System.out.println(others);
            }
        }

    }

    private static class ModelServerConfigFileObject extends SimpleJavaFileObject {
        private final String sourceCode;

        public ModelServerConfigFileObject(String projectName, String modelPath) {
            super(URI.create("file:///ModelServerConfiguration" + Kind.SOURCE.extension), Kind.SOURCE);
            String codeTemplate = """
                    package de.nexus.modelserver;
                                        
                    import de.nexus.modelserver.IModelServerConfiguration;
                                        
                    public class ModelServerConfiguration implements IModelServerConfiguration {
                        public String getProjectName(){
                            return "%s";
                        }
                        
                        public String getModelPath(){
                            return "%s";
                        }
                    }
                    """;
            String normalizedNetworkPath = modelPath.replace("\\", "\\\\");
            this.sourceCode = String.format(codeTemplate, projectName, normalizedNetworkPath);
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) {
            return sourceCode;
        }

        @Override
        public OutputStream openOutputStream() {
            throw new IllegalStateException();
        }

        @Override
        public InputStream openInputStream() {
            return new ByteArrayInputStream(sourceCode.getBytes());
        }
    }

    private static class ModelServerEngineFileObject extends SimpleJavaFileObject {
        private final String sourceCode;

        public ModelServerEngineFileObject(String projectName, String hipeNetworkPath) {
            super(URI.create("file:///ModelServerEngine" + Kind.SOURCE.extension), Kind.SOURCE);
            String codeTemplate = """
                    package de.nexus.modelserver;
                                        
                    import de.nexus.modelserver.IModelServerEngine;
                    import %s.hipe.engine.HiPEEngine;
                    import hipe.engine.HiPEOptions;
                    import %s.%sPackage;
                                        
                    public class ModelServerEngine extends HiPEEngine implements IModelServerEngine {
                        @Override
                        protected String getNetworkFilePath() {
                            return "%s";
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
                    }
                    """;
            String normalizedNetworkPath = hipeNetworkPath.replace("\\", "\\\\");
            this.sourceCode = String.format(codeTemplate, projectName, projectName, projectName, normalizedNetworkPath, projectName);
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) {
            return sourceCode;
        }

        @Override
        public OutputStream openOutputStream() {
            throw new IllegalStateException();
        }

        @Override
        public InputStream openInputStream() {
            return new ByteArrayInputStream(sourceCode.getBytes());
        }
    }

    private void packageModelServer() {
        Manifest manifest = new Manifest();
        manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
        manifest.getMainAttributes().put(Attributes.Name.MAIN_CLASS, "de.nexus.modelserver.ModelServer");
        manifest.getMainAttributes().put(Attributes.Name.CLASS_PATH, "mmlcli.jar");

        Path jarPath = this.locationRegistry.getBinPath().resolve("model-server.jar");

        try {
            JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(jarPath.toFile()), manifest);

            Files.walkFileTree(locationRegistry.getBinPath(), EnumSet.of(FileVisitOption.FOLLOW_LINKS), Integer.MAX_VALUE,
                    new SimpleFileVisitor<>() {
                        @Override
                        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                                throws IOException {

                            if (!FilenameUtils.getExtension(file.toString()).equals("class")) {
                                return FileVisitResult.CONTINUE;
                            }
                            String relativeJarPath = locationRegistry.getBinPath().relativize(file).toString();

                            System.out.println("Visiting " + file);
                            System.out.println("=> " + relativeJarPath);

                            addFileToJarOutput(jarOutputStream, relativeJarPath, file);
                            return FileVisitResult.CONTINUE;
                        }
                    });

            try {
                URL modelServerResource = getClass().getResource("/de/nexus/modelserver/");
                if (modelServerResource == null) {
                    throw new RuntimeException();
                }
                Path modelServerSourceDir = new File(modelServerResource.getFile()).toPath();
                Path modelServerPackageBase = modelServerSourceDir.getParent().getParent().getParent();
                Files.walkFileTree(modelServerSourceDir, EnumSet.of(FileVisitOption.FOLLOW_LINKS), Integer.MAX_VALUE,
                        new SimpleFileVisitor<>() {
                            @Override
                            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                                if (!FilenameUtils.getExtension(file.toString()).equals("class")) {
                                    return FileVisitResult.CONTINUE;
                                }
                                String relativeJarPath = modelServerPackageBase.relativize(file).toString();

                                addFileToJarOutput(jarOutputStream, relativeJarPath, file);
                                return FileVisitResult.CONTINUE;
                            }
                        });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            jarOutputStream.close();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

    }

    private void addFileToJarOutput(JarOutputStream target, String jarTargetPath, Path sourceFile) throws IOException {
        String name = jarTargetPath.replace("\\", "/");
        JarEntry entry = new JarEntry(name);
        entry.setTime(sourceFile.toFile().lastModified());
        target.putNextEntry(entry);

        BufferedInputStream in = new BufferedInputStream(new FileInputStream(sourceFile.toFile()));
        byte[] buffer = new byte[1024];
        while (true) {
            int count = in.read(buffer);
            if (count == -1) {
                break;
            }
            target.write(buffer, 0, count);
        }
        target.closeEntry();
        in.close();
    }
}
