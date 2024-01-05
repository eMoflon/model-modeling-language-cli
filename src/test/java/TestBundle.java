import java.io.File;

public class TestBundle {
    private final String bundleName;
    private final File ecoreFile;
    private final File serializedFile;

    public TestBundle(String bundleName, File ecoreFile, File serializedFile) {
        this.bundleName = bundleName;
        this.ecoreFile = ecoreFile;
        this.serializedFile = serializedFile;
    }

    public String getBundleName() {
        return bundleName;
    }

    public File getEcoreFile() {
        return ecoreFile;
    }

    public File getSerializedFile() {
        return serializedFile;
    }
}
