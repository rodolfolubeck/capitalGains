package org.example.integration;

import org.example.Main;
import org.example.integration.fixtures.OperationFixtures;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MainIntegrationTest {

    private static final String INPUT_DIRECTORY = "src/test/resources/test/inputs";
    private static final String OUTPUT_DIRECTORY = "src/test/resources/test/outputs";

    @Test
    public void testMainIntegrationWithJsonInputAndExpectedOutput() throws Exception {
        Path inputDir = Paths.get(INPUT_DIRECTORY);
        Path outputDir = Paths.get(OUTPUT_DIRECTORY);

        try (var paths = Files.walk(inputDir)) {
            paths.filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".json"))
                    .forEach(inputFile -> {
                        try {
                            String jsonInput = OperationFixtures.loadJsonInput(inputFile.toString());

                            String expectedJsonOutput = OperationFixtures.loadJsonOutput(outputDir.resolve(inputFile.getFileName()).toString());

                            ByteArrayInputStream inputStream = new ByteArrayInputStream(jsonInput.getBytes());
                            System.setIn(inputStream);

                            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                            PrintStream printStream = new PrintStream(outputStream);
                            System.setOut(printStream);

                            System.out.println("Processando arquivo: " + inputFile.getFileName());

                            Main.main(new String[]{});

                            String actualJsonOutput = outputStream.toString().trim();

                            assertEquals(expectedJsonOutput, actualJsonOutput);

                            System.out.println("Resultado: Sucesso");

                        } catch (Exception e) {
                            System.out.println("Erro ao processar o arquivo: " + inputFile.getFileName());
                            e.printStackTrace();
                        }
                    });
        }
    }
}
