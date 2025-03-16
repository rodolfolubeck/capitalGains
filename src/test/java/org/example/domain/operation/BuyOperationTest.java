package org.example.domain.operation;

import org.example.services.portfolio.PortfolioService;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

public class BuyOperationTest {

    @Test
    public void testGetUnitCost() {
        BuyOperation buyOperation = new BuyOperation(10.50, 100);

        assertEquals(10.50, buyOperation.unitCost(), "O custo unitário da compra está incorreto.");
    }

    @Test
    public void testGetQuantity() {
        BuyOperation buyOperation = new BuyOperation(10.50, 100);

        assertEquals(100, buyOperation.quantity(), "A quantidade da compra está incorreta.");
    }

    @Test
    public void testGetType() {
        BuyOperation buyOperation = new BuyOperation(10.50, 100);

        assertEquals("buy", buyOperation.getType(), "O tipo da operação está incorreto.");
    }

    @Test
    public void testExecute() {
        PortfolioService portfolioService = mock(PortfolioService.class);
        BuyOperation buyOperation = new BuyOperation(10.50, 100);

        buyOperation.execute(portfolioService);

        verify(portfolioService, times(1)).addBuy(buyOperation);
    }
}
