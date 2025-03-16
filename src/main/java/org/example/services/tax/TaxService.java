package org.example.services.tax;

public class TaxService {

    public double calculateTax(double profitOrLoss, double totalSellValue) {
        if (profitOrLoss > 0 && totalSellValue > 20000.00) {
            return profitOrLoss * 0.20;
        }
        return 0;
    }
}
