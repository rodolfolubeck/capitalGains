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

        main = new Main(portfolioService, objectMapper);
    }

    @Test
    public void testProcessOperations() throws Exception {
        Mockito.when(buyOperationDTO.getOperation()).thenReturn("buy");
        Mockito.when(buyOperationDTO.getUnitCost()).thenReturn(10.00);
        Mockito.when(buyOperationDTO.getQuantity()).thenReturn(100);

        Mockito.when(sellOperationDTO1.getOperation()).thenReturn("sell");
        Mockito.when(sellOperationDTO1.getUnitCost()).thenReturn(15.00);
        Mockito.when(sellOperationDTO1.getQuantity()).thenReturn(50);

        Mockito.when(sellOperationDTO2.getOperation()).thenReturn("sell");
        Mockito.when(sellOperationDTO2.getUnitCost()).thenReturn(15.00);
        Mockito.when(sellOperationDTO2.getQuantity()).thenReturn(50);

        Operation buyOperation = new BuyOperation(10.00, 100);
        Operation sellOperation1 = new SellOperation(15.00, 50);
        Operation sellOperation2 = new SellOperation(15.00, 50);

        Mockito.when(buyOperationDTO.toOperation()).thenReturn(buyOperation);
        Mockito.when(sellOperationDTO1.toOperation()).thenReturn(sellOperation1);
        Mockito.when(sellOperationDTO2.toOperation()).thenReturn(sellOperation2);

        String jsonInput = "[{\"operation\":\"buy\", \"unit-cost\":10.00, \"quantity\":100},"
                + "{\"operation\":\"sell\", \"unit-cost\":15.00, \"quantity\":50},"
                + "{\"operation\":\"sell\", \"unit-cost\":15.00, \"quantity\":50}]";

        List<List<TaxResultDTO>> allTaxes = main.processOperations(jsonInput);

        Mockito.verify(portfolioService, Mockito.times(2)).processSell(Mockito.any(SellOperation.class));

        List<TaxResultDTO> expectedTaxesGroup1 = List.of(
                new TaxResultDTO(0.00),
                new TaxResultDTO(0.00),
                new TaxResultDTO(0.00)
        );

        for (int i = 0; i < allTaxes.get(0).size(); i++) {
            assertEquals(expectedTaxesGroup1.get(i).getTax(), allTaxes.get(0).get(i).getTax(), 0.01);
        }
    }
}
