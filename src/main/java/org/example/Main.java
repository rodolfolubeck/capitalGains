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

        String[] operationsGroups = jsonInput.split("\\]\\s*\\[");
        List<TaxResultDTO> taxes = new ArrayList<>();

        for (String operationsGroup : operationsGroups) {
            if (!operationsGroup.startsWith("[")) {
                operationsGroup = "[" + operationsGroup;
            }
            if (!operationsGroup.endsWith("]")) {
                operationsGroup = operationsGroup + "]";
            }

            List<OperationDTO> operationsDTO = objectMapper.readValue(operationsGroup, objectMapper.getTypeFactory()
                    .constructCollectionType(List.class, OperationDTO.class));

            for (OperationDTO operationDTO : operationsDTO) {
                Operation operation = operationDTO.toOperation();
                if ("buy".equals(operation.getType())) {
                    operation.execute(portfolioService);
                    taxes.add(new TaxResultDTO(0.00));
                } else if ("sell".equals(operation.getType())) {
                    double tax = portfolioService.processSell((SellOperation) operation);
                    taxes.add(new TaxResultDTO(tax));
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
            System.out.println("Digite o JSON das operações (pressione ENTER para finalizar):");
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
