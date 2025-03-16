package org.example.integration.fixtures;

import java.nio.file.Files;
import java.nio.file.Paths;

public class OperationFixtures {

    public static String loadJsonInput(String fileName) throws Exception {
        return new String(Files.readAllBytes(Paths.get(fileName)));
    }

    public static String loadJsonOutput(String fileName) throws Exception {
        return new String(Files.readAllBytes(Paths.get(fileName)));
    }
}
