package org.example.integration;

import org.example.Main;
import org.example.integration.fixtures.OperationFixtures;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MainIntegrationTest {

    private static final String INPUT_DIRECTORY = "src/test/resources/test/inputs"; // Substitua com o diretório correto
    private static final String OUTPUT_DIRECTORY = "src/test/resources/test/outputs"; // Substitua com o diretório correto

    @Test
    public void testMainIntegrationWithJsonInputAndExpectedOutput() throws Exception {
        // Caminho para o diretório de arquivos de entrada
        Path inputDir = Paths.get(INPUT_DIRECTORY);
        Path outputDir = Paths.get(OUTPUT_DIRECTORY);

        // Listar todos os arquivos .json no diretório de entrada
        try (var paths = Files.walk(inputDir)) {
            paths.filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".json"))
                    .forEach(inputFile -> {
                        try {
                            // Carrega o JSON de entrada
                            String jsonInput = OperationFixtures.loadJsonInput(inputFile.toString());

                            // Carrega o JSON de saída esperado
                            String expectedJsonOutput = OperationFixtures.loadJsonOutput(outputDir.resolve(inputFile.getFileName()).toString());

                            // Redireciona a entrada (System.in) para o JSON de entrada
                            ByteArrayInputStream inputStream = new ByteArrayInputStream(jsonInput.getBytes());
                            System.setIn(inputStream);

                            // Redireciona a saída (System.out) para capturar a saída gerada
                            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                            PrintStream printStream = new PrintStream(outputStream);
                            System.setOut(printStream);

                            // Imprime o nome do arquivo que está sendo processado
                            System.out.println("Processando arquivo: " + inputFile.getFileName());

                            // Executa o método main da sua classe
                            Main.main(new String[]{});

                            // Obtém a saída gerada
                            String actualJsonOutput = outputStream.toString().trim();

                            // Compara a saída esperada com a saída gerada
                            assertEquals(expectedJsonOutput, actualJsonOutput);

                            // Imprime "Sucesso" caso a saída seja igual à esperada
                            System.out.println("Resultado: Sucesso");

                        } catch (Exception e) {
                            // Imprime "Erro" caso ocorra uma exceção
                            System.out.println("Erro ao processar o arquivo: " + inputFile.getFileName());
                            e.printStackTrace();
                        }
                    });
        }
    }
}
