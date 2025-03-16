package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import org.example.domain.operation.dto.OperationDTO;
import org.example.domain.dto.TaxResultDTO;
import org.example.domain.operation.Operation;
import org.example.domain.operation.SellOperation;
import org.example.services.portfolio.PortfolioService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private final PortfolioService portfolioService;
    private final ObjectMapper objectMapper;

    public Main(PortfolioService portfolioService, ObjectMapper objectMapper) {
        this.portfolioService = portfolioService;
        this.objectMapper = objectMapper;
    }

    public Main() {
        this(new PortfolioService(), new ObjectMapper());
    }

    public List<List<TaxResultDTO>> processOperations(String jsonInput) throws IOException {
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.KEBAB_CASE);

        // Separar os grupos de operações por cada par de colchetes
        String[] operationsGroups = jsonInput.split("\\]\\s*\\[");
        List<List<TaxResultDTO>> allTaxes = new ArrayList<>();  // Lista de listas para cada grupo de impostos

        // Processar cada grupo de operações separadamente
        for (String operationsGroup : operationsGroups) {
            // Garantir que o grupo de operações esteja no formato correto
            if (!operationsGroup.startsWith("[")) {
                operationsGroup = "[" + operationsGroup;
            }
            if (!operationsGroup.endsWith("]")) {
                operationsGroup = operationsGroup + "]";
            }

            // Converter o grupo de operações para objetos OperationDTO
            List<OperationDTO> operationsDTO = objectMapper.readValue(operationsGroup, objectMapper.getTypeFactory()
                    .constructCollectionType(List.class, OperationDTO.class));

            // Processar cada operação dentro do grupo
            List<TaxResultDTO> groupTaxes = new ArrayList<>(); // Lista para armazenar os impostos de cada grupo
            for (OperationDTO operationDTO : operationsDTO) {
                Operation operation = operationDTO.toOperation();
                if ("buy".equals(operation.getType())) {
                    operation.execute(portfolioService);
                    groupTaxes.add(new TaxResultDTO(0.00)); // Nenhum imposto para operações de compra
                } else if ("sell".equals(operation.getType())) {
                    double tax = portfolioService.processSell((SellOperation) operation);
                    groupTaxes.add(new TaxResultDTO(tax)); // Adiciona imposto para operações de venda
                }
            }

            // Adiciona a lista de impostos do grupo de operações na lista geral
            allTaxes.add(groupTaxes);
        }

        return allTaxes;
    }

    public static void main(String[] args) {
        PortfolioService portfolioService = new PortfolioService();
        ObjectMapper objectMapper = new ObjectMapper();
        Main main = new Main(portfolioService, objectMapper);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            StringBuilder input = new StringBuilder();

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    break;
                }
                input.append(line).append("\n");
            }

            // Processa as operações, agora retornando uma lista de listas de resultados
            List<List<TaxResultDTO>> allTaxes = main.processOperations(input.toString().trim());

            // Para cada grupo de impostos, converta para JSON e imprima separadamente
            for (List<TaxResultDTO> groupTaxes : allTaxes) {
                System.out.println(objectMapper.writeValueAsString(groupTaxes));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
