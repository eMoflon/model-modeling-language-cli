import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class TestUtils {
    public static ArrayList<TestBundle> getTestBundles(File dataFolder) {
        if (dataFolder == null || !dataFolder.exists() || !dataFolder.isDirectory()) {
            throw new IllegalArgumentException("Data folder is not directory or does not exist!");
        }

        Set<String> knownFilenames = new HashSet<>();
        ArrayList<TestBundle> testBundles = new ArrayList<>();

        for (File file : Objects.requireNonNull(dataFolder.listFiles())) {
            String fileName = FilenameUtils.getBaseName(file.getName());
            String extension = FilenameUtils.getExtension(file.getName());

            if (knownFilenames.contains(fileName)) {
                continue;
            }
            knownFilenames.add(fileName);

            File ecoreFile, jsonFile;

            if (extension.equals("ecore")) {
                ecoreFile = file;
                jsonFile = file.toPath().resolveSibling(fileName + ".json").toFile();
                if (!jsonFile.exists() || jsonFile.isDirectory()) {
                    System.err.println("Skipping file due to missing json match: " + file.getAbsolutePath());
                    continue;
                }
            } else if (extension.equals("json")) {
                ecoreFile = file.toPath().resolveSibling(fileName + ".ecore").toFile();
                jsonFile = file;
                if (!ecoreFile.exists() || ecoreFile.isDirectory()) {
                    System.err.println("Skipping file due to missing ecore match: " + file.getAbsolutePath());
                    continue;
                }
            } else {
                System.err.println("Skipping file due to extension mismatch: " + file.getAbsolutePath());
                continue;
            }
            testBundles.add(new TestBundle(fileName, ecoreFile, jsonFile));
        }
        return testBundles;
    }
}
