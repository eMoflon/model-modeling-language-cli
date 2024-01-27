package serializer;

import de.nexus.mmlcli.generator.EmfResourceBuilder;
import de.nexus.mmlcli.generator.SerializedDocument;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.stream.Stream;

public class GeneratorTests {
    private final static File TEST_DATA_FOLDER = new File("src/test/data");

    static Stream<TestBundle> testBundleProvider() {
        return TestUtils.getTestBundles(TEST_DATA_FOLDER).stream();
    }

    @ParameterizedTest
    @MethodSource("testBundleProvider")
    void testCorrectEcoreGeneration(TestBundle bundle, @TempDir File workingDir) throws IOException {
        String serializedContent = Files.readString(bundle.getSerializedFile().toPath());

        SerializedDocument[] result = Objects.requireNonNull(SerializedDocument.deserialize(serializedContent));
        EmfResourceBuilder.buildEmfResources(result, bundle.getBundleName(), workingDir);

        String ecoreContent = Files.readString(bundle.getEcoreFile().toPath());

        Path generatedEcore = workingDir.toPath().resolve("model").resolve(bundle.getBundleName() + "_" + bundle.getBundleName() + ".ecore");

        Assertions.assertTrue(generatedEcore.toFile().exists());

        String generatedEcoreContent = Files.readString(generatedEcore);

        Assertions.assertEquals(ecoreContent, generatedEcoreContent);
    }
}
