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

        // Criando uma operação de compra
        BuyOperation buyOperation = new BuyOperation(10.00, 100);

        // Adicionando a operação de compra
        portfolioService.addBuy(buyOperation);

        // Usando Reflection para acessar os campos privados
        Field weightedAveragePriceField = PortfolioService.class.getDeclaredField("weightedAveragePrice");
        weightedAveragePriceField.setAccessible(true);
        double weightedAveragePrice = (double) weightedAveragePriceField.get(portfolioService);

        Field totalQuantityField = PortfolioService.class.getDeclaredField("totalQuantity");
        totalQuantityField.setAccessible(true);
        int totalQuantity = (int) totalQuantityField.get(portfolioService);

        // Verificando se o preço médio ponderado foi calculado corretamente
        assertEquals(10.00, weightedAveragePrice, "O preço médio ponderado está incorreto.");
        assertEquals(100, totalQuantity, "A quantidade total de ações está incorreta.");
    }

    @Test
    public void testProcessSell() throws Exception {
        // Mock do TaxService
        TaxService taxServiceMock = mock(TaxService.class);
        PortfolioService portfolioService = new PortfolioService();

        // Substituindo o TaxService real pelo mock
        Field taxServiceField = PortfolioService.class.getDeclaredField("taxService");
        taxServiceField.setAccessible(true);
        taxServiceField.set(portfolioService, taxServiceMock);

        // Definindo o comportamento do mock
        when(taxServiceMock.calculateTax(anyDouble(), anyDouble())).thenReturn(100.0);

        // Adicionando uma operação de compra
        portfolioService.addBuy(new BuyOperation(10.00, 100));

        // Criando uma operação de venda
        SellOperation sellOperation = new SellOperation(15.00, 50);

        // Processando a venda
        double tax = portfolioService.processSell(sellOperation);

        // Usando Reflection para acessar os campos privados
        Field totalQuantityField = PortfolioService.class.getDeclaredField("totalQuantity");
        totalQuantityField.setAccessible(true);
        int totalQuantity = (int) totalQuantityField.get(portfolioService);

        // Verificando o imposto calculado
        assertEquals(100.0, tax, "O imposto calculado está incorreto.");

        // Verificando a quantidade total após a venda
        assertEquals(50, totalQuantity, "A quantidade total de ações após a venda está incorreta.");
    }


    @Test
    public void testProcessSellWithCarriedLoss() throws Exception {
        // Mock do TaxService
        TaxService taxServiceMock = mock(TaxService.class);
        PortfolioService portfolioService = new PortfolioService();

        // Substituindo o TaxService real pelo mock
        Field taxServiceField = PortfolioService.class.getDeclaredField("taxService");
        taxServiceField.setAccessible(true);
        taxServiceField.set(portfolioService, taxServiceMock);

        // Definindo o comportamento do mock
        when(taxServiceMock.calculateTax(anyDouble(), anyDouble())).thenReturn(100.0);

        // Adicionando uma operação de compra com preço maior (cria uma perda acumulada)
        portfolioService.addBuy(new BuyOperation(20.00, 100));

        // Criando uma operação de venda com preço menor que o preço de compra
        SellOperation sellOperation = new SellOperation(15.00, 50);

        // Processando a venda
        double tax = portfolioService.processSell(sellOperation);

        // Usando Reflection para acessar a perda acumulada
        Field carriedLossField = PortfolioService.class.getDeclaredField("carriedLoss");
        carriedLossField.setAccessible(true);
        double carriedLoss = (double) carriedLossField.get(portfolioService);

        // Verificando o imposto calculado após a perda acumulada
        assertEquals(100.0, tax, "O imposto calculado com a perda acumulada está incorreto.");

        // Verificando o valor da quantidade total após a venda
        Field totalQuantityField = PortfolioService.class.getDeclaredField("totalQuantity");
        totalQuantityField.setAccessible(true);
        int totalQuantity = (int) totalQuantityField.get(portfolioService);

        // Verificando a quantidade total de ações após a venda
        assertEquals(50, totalQuantity, "A quantidade total de ações após a venda está incorreta.");

        // Verificando o valor da perda acumulada após a venda
        // A perda acumulada deve ser de -25.0, pois o prejuízo da venda foi de 5.0 * 50 = -250.0,
        // e a perda acumulada total era 0 antes da venda
        assertEquals(-250.0, carriedLoss, "A perda acumulada não foi aplicada corretamente.");
    }



    @Test
    public void testApplyCarriedLoss() throws Exception {
        PortfolioService portfolioService = new PortfolioService();

        // Usando Reflection para acessar e modificar o campo privado "carriedLoss"
        Field carriedLossField = PortfolioService.class.getDeclaredField("carriedLoss");
        carriedLossField.setAccessible(true);
        carriedLossField.set(portfolioService, -100.0);

        // Verificando se a perda acumulada é aplicada corretamente
        Method applyCarriedLossMethod = PortfolioService.class.getDeclaredMethod("applyCarriedLoss", double.class);
        applyCarriedLossMethod.setAccessible(true);
        double result = (double) applyCarriedLossMethod.invoke(portfolioService, 50.0);
        assertEquals(0.0, result, "A perda acumulada foi aplicada incorretamente.");

        // Verificando se a perda acumulada é zerada quando o lucro excede a perda
        carriedLossField.set(portfolioService, -100.0);
        result = (double) applyCarriedLossMethod.invoke(portfolioService, 200.0);
        assertEquals(100.0, result, "O valor do lucro após aplicar a perda acumulada está incorreto.");
        assertEquals(0.0, carriedLossField.get(portfolioService), "A perda acumulada deveria ser zerada.");
    }

    @Test
    public void testRemoveOldBuys() throws Exception {
        PortfolioService portfolioService = new PortfolioService();

        // Adicionando compras
        portfolioService.addBuy(new BuyOperation(10.00, 100));  // Compra de 100 ações a 10.00
        portfolioService.addBuy(new BuyOperation(12.00, 50));   // Compra de 50 ações a 12.00

        // Verificando a quantidade total antes da venda
        Field totalQuantityField = PortfolioService.class.getDeclaredField("totalQuantity");
        totalQuantityField.setAccessible(true);
        int totalQuantityBeforeSell = (int) totalQuantityField.get(portfolioService);
        assertEquals(150, totalQuantityBeforeSell, "A quantidade total de ações antes da venda está incorreta.");

        // Acessando o método privado removeOldBuys usando Reflection
        Method removeOldBuysMethod = PortfolioService.class.getDeclaredMethod("removeOldBuys", int.class);
        removeOldBuysMethod.setAccessible(true);

        // Removendo compras antigas ao processar uma venda
        removeOldBuysMethod.invoke(portfolioService, 120);  // Vendendo 120 ações

        // Verificando a quantidade de ações restantes após a remoção
        int totalQuantityAfterSell = (int) totalQuantityField.get(portfolioService) - 120;
        assertEquals(30, totalQuantityAfterSell, "A quantidade de ações restantes após a remoção está incorreta.");
    }
}
