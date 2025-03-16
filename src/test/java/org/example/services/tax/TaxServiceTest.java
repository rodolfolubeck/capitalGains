package org.example.services.tax;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TaxServiceTest {

    @Test
    public void testCalculateTax_withProfitAndHighSellValue() {
        TaxService taxService = new TaxService();

        double profitOrLoss = 1000.0;
        double totalSellValue = 25000.0;

        double tax = taxService.calculateTax(profitOrLoss, totalSellValue);

        assertEquals(200.0, tax, "O imposto calculado está incorreto.");
    }

    @Test
    public void testCalculateTax_withProfitAndLowSellValue() {
        TaxService taxService = new TaxService();

        double profitOrLoss = 1000.0;
        double totalSellValue = 15000.0;

        double tax = taxService.calculateTax(profitOrLoss, totalSellValue);

        assertEquals(0.0, tax, "O imposto calculado deve ser 0.");
    }

    @Test
    public void testCalculateTax_withNoProfit() {
        TaxService taxService = new TaxService();

        double profitOrLoss = -1000.0;
        double totalSellValue = 25000.0;

        double tax = taxService.calculateTax(profitOrLoss, totalSellValue);

        // O imposto deve ser 0 pois o lucro é negativo
        assertEquals(0.0, tax, "O imposto calculado deve ser 0 para prejuízo.");
    }

    @Test
    public void testCalculateTax_withZeroProfit() {
        TaxService taxService = new TaxService();

        double profitOrLoss = 0.0;
        double totalSellValue = 25000.0;

        double tax = taxService.calculateTax(profitOrLoss, totalSellValue);

        assertEquals(0.0, tax, "O imposto calculado deve ser 0 para lucro zero.");
    }
}
