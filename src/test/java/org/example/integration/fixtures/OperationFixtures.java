package org.example.integration.fixtures;

import java.nio.file.Files;
import java.nio.file.Paths;

public class OperationFixtures {

    // Carrega o JSON de entrada de um arquivo
    public static String loadJsonInput(String fileName) throws Exception {
        return new String(Files.readAllBytes(Paths.get(fileName)));
    }

    // Carrega o JSON de saída de um arquivo
    public static String loadJsonOutput(String fileName) throws Exception {
        return new String(Files.readAllBytes(Paths.get(fileName)));
    }
}
