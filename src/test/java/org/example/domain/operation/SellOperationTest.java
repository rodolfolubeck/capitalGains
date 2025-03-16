package org.example.domain.operation;

import org.example.services.portfolio.PortfolioService;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class SellOperationTest {

    @Test
    public void testGetType() {
        SellOperation sellOperation = new SellOperation(15.00, 50);
        assertEquals("sell", sellOperation.getType(), "O tipo da operação deve ser 'sell'.");
    }

    @Test
    public void testExecute() {
        PortfolioService portfolioService = mock(PortfolioService.class);

        SellOperation sellOperation = new SellOperation(15.00, 50);

        when(portfolioService.processSell(sellOperation)).thenReturn(100.00); // Exemplo de valor do imposto retornado

        sellOperation.execute(portfolioService);

        verify(portfolioService, times(1)).processSell(sellOperation);
    }

    @Test
    public void testExecuteWithNullPortfolioService() {
        SellOperation sellOperation = new SellOperation(15.00, 50);

        assertThrows(NullPointerException.class, () -> {
            sellOperation.execute(null);
        });
    }
}
