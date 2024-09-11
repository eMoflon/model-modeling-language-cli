package de.nexus.mmlcli;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CommandUtilsTest {
    @Test
    void loadsDataFromStdIn() {
        System.setIn(new ByteArrayInputStream("test data".getBytes()));

        Optional<String> loadedData = CommandUtils.loadDataFromFileOrStdIn(null);

        assertTrue(loadedData.isPresent());
        assertEquals("test data", loadedData.get());
    }

    @Test
    void loadsDataFromFile(@TempDir File tempDir) throws IOException {
        File testFile = new File(tempDir, "test.txt");
        Files.writeString(testFile.toPath(), "data from file");

        Optional<String> loadedData = CommandUtils.loadDataFromFileOrStdIn(testFile);

        assertTrue(loadedData.isPresent());
        assertEquals("data from file", loadedData.get());
    }

    @Test
    void loadDataHandlesMissingFile(@TempDir File tempDir) {
        File nonExistentFile = new File(tempDir, "nonexistent.txt");

        Optional<String> loadedData = CommandUtils.loadDataFromFileOrStdIn(nonExistentFile);

        assertTrue(loadedData.isEmpty());
    }

    @Test
    @Disabled("SetReadable is not supported by every operating system")
    void loadDataHandlesUnreadableFile(@TempDir File tempDir) throws IOException {
        File unreadableFile = new File(tempDir, "unreadable.txt");
        unreadableFile.createNewFile();
        unreadableFile.setReadable(false);

        Optional<String> loadedData = CommandUtils.loadDataFromFileOrStdIn(unreadableFile);

        assertTrue(loadedData.isEmpty());
    }

    @Test
    void loadDataHandlesIOExceptionDuringRead(@TempDir File tempDir) throws IOException {
        File testFile = new File(tempDir, "test.txt");
        Files.writeString(testFile.toPath(), "data from file");

        try (MockedStatic<Files> mockedFiles = Mockito.mockStatic(Files.class)) {
            mockedFiles.when(() -> Files.readString(testFile.toPath(), StandardCharsets.UTF_8)).thenThrow(new IOException());

            Optional<String> loadedData = CommandUtils.loadDataFromFileOrStdIn(testFile);
            assertTrue(loadedData.isEmpty());
        }


    }
}