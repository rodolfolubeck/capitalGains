package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import org.example.domain.Operation;
import org.example.domain.dto.OperationDTO;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class Main {
    public static void main(String[] args) {
//        Input para exemplo:
//        [{"operation":"buy", "unit-cost":10.00, "quantity": 100},
//        {"operation":"sell", "unit-cost":15.00, "quantity": 50},
//        {"operation":"sell", "unit-cost":15.00, "quantity": 50}]

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.KEBAB_CASE);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            System.out.println("Digite o JSON das operações (pressione ENTER para finalizar):");
            StringBuilder input = new StringBuilder();

            String line;
            while ((line = reader.readLine()) != null && !line.trim().isEmpty()) {
                input.append(line).append("\n");
            }

            String jsonInput = input.toString().trim();
            List<OperationDTO> operationsDTO = objectMapper.readValue(jsonInput, objectMapper.getTypeFactory()
                    .constructCollectionType(List.class, OperationDTO.class));

            for (OperationDTO operationDTO : operationsDTO) {
                Operation operation = operationDTO.toOperation();
                operation.execute();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
