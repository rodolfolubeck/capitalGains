package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import org.example.domain.operation.BuyOperation;
import org.example.domain.operation.dto.OperationDTO;
import org.example.domain.dto.TaxResultDTO;
import org.example.domain.operation.Operation;
import org.example.domain.operation.SellOperation;
import org.example.services.portfolio.PortfolioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayInputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MainTest {

    @Mock
    private PortfolioService portfolioService;

    @Mock
    private OperationDTO buyOperationDTO;

    @Mock
    private OperationDTO sellOperationDTO1;

    @Mock
    private OperationDTO sellOperationDTO2;

    private ObjectMapper objectMapper;
    private Main main;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.KEBAB_CASE);

        // Criando instância de Main com mocks
        main = new Main(portfolioService, objectMapper);
    }

    @Test
    public void testProcessOperations() throws Exception {
        // Definindo os mocks para simular o comportamento do DTO
        Mockito.when(buyOperationDTO.getOperation()).thenReturn("buy");
        Mockito.when(buyOperationDTO.getUnitCost()).thenReturn(10.00);
        Mockito.when(buyOperationDTO.getQuantity()).thenReturn(100);

        Mockito.when(sellOperationDTO1.getOperation()).thenReturn("sell");
        Mockito.when(sellOperationDTO1.getUnitCost()).thenReturn(15.00);
        Mockito.when(sellOperationDTO1.getQuantity()).thenReturn(50);

        Mockito.when(sellOperationDTO2.getOperation()).thenReturn("sell");
        Mockito.when(sellOperationDTO2.getUnitCost()).thenReturn(15.00);
        Mockito.when(sellOperationDTO2.getQuantity()).thenReturn(50);

        // Mock para o método toOperation
        Operation buyOperation = new BuyOperation(10.00, 100);
        Operation sellOperation1 = new SellOperation(15.00, 50);
        Operation sellOperation2 = new SellOperation(15.00, 50);

        Mockito.when(buyOperationDTO.toOperation()).thenReturn(buyOperation);
        Mockito.when(sellOperationDTO1.toOperation()).thenReturn(sellOperation1);
        Mockito.when(sellOperationDTO2.toOperation()).thenReturn(sellOperation2);

        // Configuração do fluxo de entrada do JSON
        String jsonInput = "[{\"operation\":\"buy\", \"unit-cost\":10.00, \"quantity\":100},"
                + "{\"operation\":\"sell\", \"unit-cost\":15.00, \"quantity\":50},"
                + "{\"operation\":\"sell\", \"unit-cost\":15.00, \"quantity\":50}]";

        // Chamando o método processOperations diretamente
        List<TaxResultDTO> taxes = main.processOperations(jsonInput);

        // Verificar se as operações foram processadas corretamente
        Mockito.verify(portfolioService, Mockito.times(2)).processSell(Mockito.any(SellOperation.class));

        // A saída esperada de TaxResultDTO pode ser ajustada com base nos cálculos do imposto
        List<TaxResultDTO> expectedTaxes = List.of(
                new TaxResultDTO(0.00),  // Imposto para a operação de compra
                new TaxResultDTO(0.00),  // Imposto para a primeira venda
                new TaxResultDTO(0.00)   // Imposto para a segunda venda
        );

        // Comparar diretamente os valores de tax, sem se preocupar com as instâncias
        for (int i = 0; i < taxes.size(); i++) {
            assertEquals(expectedTaxes.get(i).getTax(), taxes.get(i).getTax(), 0.01);  // Comparação de valores com uma margem de erro de 0.01
        }
    }
}
