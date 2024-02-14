package de.nexus.mmlcli.constraint.adapter;

import de.nexus.mmlcli.constraint.adapter.codegen.ConstraintGenerator;
import de.nexus.mmlcli.constraint.adapter.codegen.ModelServerConfigurationGenerator;
import de.nexus.mmlcli.constraint.adapter.codegen.ModelServerEngineGenerator;
import de.nexus.mmlcli.constraint.adapter.codegen.PatternInitializerGenerator;
import de.nexus.mmlcli.constraint.entity.ConstraintDocumentEntity;
import org.apache.commons.io.FilenameUtils;

import javax.tools.*;
import java.io.*;
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
    private final ConstraintDocumentEntity cDoc;
    private final String projectName;
    private final boolean verbose;

    public ModelServerBuilder(LocationRegistry locationRegistry, String projectName, ConstraintDocumentEntity cDoc, boolean verbose) {
        this.locationRegistry = locationRegistry;
        this.projectName = projectName;
        this.cDoc = cDoc;
        this.verbose = verbose;
    }

    public void buildModelServer(boolean generateJar) {
        double tic = System.currentTimeMillis();
        System.out.println("[ModelServerBuilder] Compiling model server...");

        compileModelServer();

        double toc = System.currentTimeMillis();
        System.out.println("[ModelServerBuilder] Code compilation completed in " + (toc - tic) / 1000.0 + " seconds.");

        if (generateJar) {
            double tic2 = System.currentTimeMillis();
            System.out.println("[ModelServerBuilder] Packing model server...");

            packageModelServer();

            double toc2 = System.currentTimeMillis();
            System.out.println("[ModelServerBuilder] Code packaging completed in " + (toc2 - tic2) / 1000.0 + " seconds.");
        }
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
        ArrayList<JavaFileObject> classesToCompile = this.generateModelServerFiles();
        uncompiledClasses.forEach(classesToCompile::add);

        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, collector, options, null, classesToCompile);
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

    private ArrayList<JavaFileObject> generateModelServerFiles() {
        String hipeNetworkPath = this.locationRegistry.getSrcGenPath().resolve(this.projectName).resolve("hipe/engine/hipe-network.xmi").toString();
        ArrayList<JavaFileObject> files = new ArrayList<>();
        files.add(ModelServerConfigurationGenerator.build(this.projectName, this.locationRegistry.getModelPath().toString(), this.cDoc));
        files.add(ModelServerEngineGenerator.build(this.projectName, hipeNetworkPath));

        this.cDoc.getConstraints().forEach(constraint -> files.add(ConstraintGenerator.build(constraint)));

        files.add(PatternInitializerGenerator.build(this.cDoc.getPatterns()));

        return files;
    }

    private void packageModelServer() {
        Manifest manifest = new Manifest();
        manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
        manifest.getMainAttributes().put(Attributes.Name.MAIN_CLASS, "de.nexus.modelserver.ModelServer");

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
