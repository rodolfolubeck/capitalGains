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

    public List<TaxResultDTO> processOperations(String jsonInput) throws IOException {
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.KEBAB_CASE);

        // Separar os grupos de operações com base em um caractere de quebra
        String[] operationsGroups = jsonInput.split("\\]\\s*\\[");  // divide os grupos de operações
        List<TaxResultDTO> taxes = new ArrayList<>();

        // Processar cada grupo de operações separadamente
        for (String operationsGroup : operationsGroups) {
            // Garantir que o grupo de operações esteja no formato correto de JSON
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
            for (OperationDTO operationDTO : operationsDTO) {
                Operation operation = operationDTO.toOperation();
                if ("buy".equals(operation.getType())) {
                    operation.execute(portfolioService);
                    taxes.add(new TaxResultDTO(0.00)); // Nenhum imposto para operações de compra
                } else if ("sell".equals(operation.getType())) {
                    double tax = portfolioService.processSell((SellOperation) operation);
                    taxes.add(new TaxResultDTO(tax)); // Adiciona imposto para operações de venda
                }
            }
        }

        return taxes;
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

            List<TaxResultDTO> taxes = main.processOperations(input.toString().trim());
            System.out.println(objectMapper.writeValueAsString(taxes));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
