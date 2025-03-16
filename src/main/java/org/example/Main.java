    package org.example;

    import com.fasterxml.jackson.databind.ObjectMapper;
    import com.fasterxml.jackson.databind.PropertyNamingStrategy;
    import org.example.domain.dto.OperationDTO;
    import org.example.domain.dto.TaxResultDTO;
    import org.example.domain.operation.Operation;
    import org.example.domain.operation.SellOperation;
    import org.example.domain.portfolio.Portfolio;

    import java.io.BufferedReader;
    import java.io.IOException;
    import java.io.InputStreamReader;
    import java.util.ArrayList;
    import java.util.List;

    public class Main {
        public static void main(String[] args) {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.KEBAB_CASE);

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
                System.out.println("Digite o JSON das operações (pressione ENTER para finalizar):");
                StringBuilder input = new StringBuilder();

                //validação da linha em branco
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.trim().isEmpty()) {
                        break;
                    }
                    input.append(line).append("\n");
                }

                String jsonInput = input.toString().trim();

                // Agora vamos dividir o JSON em duas partes corretamente
                String[] operationsGroups = jsonInput.split("\\]\\s*\\["); // Divide corretamente nas listas

                for (String operationsGroup : operationsGroups) {
                    // Corrigir para adicionar os colchetes ao redor de cada grupo
                    if (!operationsGroup.startsWith("[")) {
                        operationsGroup = "[" + operationsGroup;
                    }
                    if (!operationsGroup.endsWith("]")) {
                        operationsGroup = operationsGroup + "]";
                    }

                    // Agora processamos cada grupo de operações corretamente
                    List<OperationDTO> operationsDTO = objectMapper.readValue(operationsGroup, objectMapper.getTypeFactory()
                            .constructCollectionType(List.class, OperationDTO.class));

                    Portfolio portfolio = new Portfolio();
                    List<TaxResultDTO> taxes = new ArrayList<>();

                    for (OperationDTO operationDTO : operationsDTO) {
                        Operation operation = operationDTO.toOperation();
                        if ("buy".equals(operation.getType())) {
                            operation.execute(portfolio);
                            taxes.add(new TaxResultDTO(0.00));
                        } else if ("sell".equals(operation.getType())) {
                            double tax = portfolio.processSell((SellOperation) operation);
                            taxes.add(new TaxResultDTO(tax));
                        }
                    }

                    System.out.println(objectMapper.writeValueAsString(taxes));
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

