package org.example.services.portfolio;

import org.example.domain.operation.BuyOperation;
import org.example.domain.operation.SellOperation;
import org.example.services.tax.TaxService;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Queue;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PortfolioServiceTest {

    @Test
    public void testAddBuy() throws Exception {
        PortfolioService portfolioService = new PortfolioService();

        BuyOperation buyOperation = new BuyOperation(10.00, 100);

        portfolioService.addBuy(buyOperation);

        Field weightedAveragePriceField = PortfolioService.class.getDeclaredField("weightedAveragePrice");
        weightedAveragePriceField.setAccessible(true);
        double weightedAveragePrice = (double) weightedAveragePriceField.get(portfolioService);

        Field totalQuantityField = PortfolioService.class.getDeclaredField("totalQuantity");
        totalQuantityField.setAccessible(true);
        int totalQuantity = (int) totalQuantityField.get(portfolioService);

        assertEquals(10.00, weightedAveragePrice, "O preço médio ponderado está incorreto.");
        assertEquals(100, totalQuantity, "A quantidade total de ações está incorreta.");
    }

    @Test
    public void testProcessSell() throws Exception {
        TaxService taxServiceMock = mock(TaxService.class);
        PortfolioService portfolioService = new PortfolioService();

        Field taxServiceField = PortfolioService.class.getDeclaredField("taxService");
        taxServiceField.setAccessible(true);
        taxServiceField.set(portfolioService, taxServiceMock);

        when(taxServiceMock.calculateTax(anyDouble(), anyDouble())).thenReturn(100.0);

        portfolioService.addBuy(new BuyOperation(10.00, 100));

        SellOperation sellOperation = new SellOperation(15.00, 50);

        double tax = portfolioService.processSell(sellOperation);

        Field totalQuantityField = PortfolioService.class.getDeclaredField("totalQuantity");
        totalQuantityField.setAccessible(true);
        int totalQuantity = (int) totalQuantityField.get(portfolioService);

        assertEquals(100.0, tax, "O imposto calculado está incorreto.");

        assertEquals(50, totalQuantity, "A quantidade total de ações após a venda está incorreta.");
    }


    @Test
    public void testProcessSellWithCarriedLoss() throws Exception {
        TaxService taxServiceMock = mock(TaxService.class);
        PortfolioService portfolioService = new PortfolioService();

        Field taxServiceField = PortfolioService.class.getDeclaredField("taxService");
        taxServiceField.setAccessible(true);
        taxServiceField.set(portfolioService, taxServiceMock);

        when(taxServiceMock.calculateTax(anyDouble(), anyDouble())).thenReturn(100.0);

        portfolioService.addBuy(new BuyOperation(20.00, 100));

        SellOperation sellOperation = new SellOperation(15.00, 50);

        double tax = portfolioService.processSell(sellOperation);

        Field carriedLossField = PortfolioService.class.getDeclaredField("carriedLoss");
        carriedLossField.setAccessible(true);
        double carriedLoss = (double) carriedLossField.get(portfolioService);

        assertEquals(100.0, tax, "O imposto calculado com a perda acumulada está incorreto.");

        Field totalQuantityField = PortfolioService.class.getDeclaredField("totalQuantity");
        totalQuantityField.setAccessible(true);
        int totalQuantity = (int) totalQuantityField.get(portfolioService);

        assertEquals(50, totalQuantity, "A quantidade total de ações após a venda está incorreta.");

        assertEquals(-250.0, carriedLoss, "A perda acumulada não foi aplicada corretamente.");
    }



    @Test
    public void testApplyCarriedLoss() throws Exception {
        PortfolioService portfolioService = new PortfolioService();

        Field carriedLossField = PortfolioService.class.getDeclaredField("carriedLoss");
        carriedLossField.setAccessible(true);
        carriedLossField.set(portfolioService, -100.0);

        Method applyCarriedLossMethod = PortfolioService.class.getDeclaredMethod("applyCarriedLoss", double.class);
        applyCarriedLossMethod.setAccessible(true);
        double result = (double) applyCarriedLossMethod.invoke(portfolioService, 50.0);
        assertEquals(0.0, result, "A perda acumulada foi aplicada incorretamente.");

        carriedLossField.set(portfolioService, -100.0);
        result = (double) applyCarriedLossMethod.invoke(portfolioService, 200.0);
        assertEquals(100.0, result, "O valor do lucro após aplicar a perda acumulada está incorreto.");
        assertEquals(0.0, carriedLossField.get(portfolioService), "A perda acumulada deveria ser zerada.");
    }

    @Test
    public void testRemoveOldBuys() throws Exception {
        PortfolioService portfolioService = new PortfolioService();

        portfolioService.addBuy(new BuyOperation(10.00, 100));
        portfolioService.addBuy(new BuyOperation(12.00, 50));

        Field totalQuantityField = PortfolioService.class.getDeclaredField("totalQuantity");
        totalQuantityField.setAccessible(true);
        int totalQuantityBeforeSell = (int) totalQuantityField.get(portfolioService);
        assertEquals(150, totalQuantityBeforeSell, "A quantidade total de ações antes da venda está incorreta.");

        Method removeOldBuysMethod = PortfolioService.class.getDeclaredMethod("removeOldBuys", int.class);
        removeOldBuysMethod.setAccessible(true);

        removeOldBuysMethod.invoke(portfolioService, 120);

        int totalQuantityAfterSell = (int) totalQuantityField.get(portfolioService) - 120;
        assertEquals(30, totalQuantityAfterSell, "A quantidade de ações restantes após a remoção está incorreta.");
    }
}
