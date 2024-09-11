package de.nexus.mmlcli;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Optional;
import java.util.Scanner;

public class CommandUtils {
    public static Optional<String> loadDataFromFileOrStdIn(File file) {
        String loadedData;
        if (file == null) {
            Scanner scanner = new Scanner(System.in);
            loadedData = scanner.nextLine();
            scanner.close();
        } else {
            if (!file.exists()) {
                System.err.println("Inputfile does not exist: " + file.getAbsolutePath());
                return Optional.empty();
            }
            if (!file.canRead()) {
                System.err.println("Could not read inputfile: " + file.getAbsolutePath());
                return Optional.empty();
            }
            try {
                loadedData = Files.readString(file.toPath(), StandardCharsets.UTF_8);
            } catch (IOException ex) {
                System.err.println("An error occured while reading: " + file.getAbsolutePath());
                return Optional.empty();
            }
        }
        return Optional.of(loadedData);
    }
}
