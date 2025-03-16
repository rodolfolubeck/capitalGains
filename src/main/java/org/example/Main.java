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

        String[] operationsGroups = jsonInput.split("\\]\\s*\\[");
        List<List<TaxResultDTO>> allTaxes = new ArrayList<>();

        for (String operationsGroup : operationsGroups) {
            if (!operationsGroup.startsWith("[")) {
                operationsGroup = "[" + operationsGroup;
            }
            if (!operationsGroup.endsWith("]")) {
                operationsGroup = operationsGroup + "]";
            }

            List<OperationDTO> operationsDTO = objectMapper.readValue(operationsGroup, objectMapper.getTypeFactory()
                    .constructCollectionType(List.class, OperationDTO.class));

            List<TaxResultDTO> groupTaxes = new ArrayList<>();
            for (OperationDTO operationDTO : operationsDTO) {
                Operation operation = operationDTO.toOperation();
                if ("buy".equals(operation.getType())) {
                    operation.execute(portfolioService);
                    groupTaxes.add(new TaxResultDTO(0.00));
                } else if ("sell".equals(operation.getType())) {
                    double tax = portfolioService.processSell((SellOperation) operation);
                    groupTaxes.add(new TaxResultDTO(tax));
                }
            }

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

            List<List<TaxResultDTO>> allTaxes = main.processOperations(input.toString().trim());

            for (List<TaxResultDTO> groupTaxes : allTaxes) {
                System.out.println(objectMapper.writeValueAsString(groupTaxes));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
