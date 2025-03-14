package org.example.service;

public class TaxCalculatorService {
    private static final double TAX_RATE = 0.20;

    public double calculateTax(double sellPrice, double avgBuyPrice, int quantity) {
        double profitPerUnit = sellPrice - avgBuyPrice;
        return profitPerUnit > 0 ? profitPerUnit * quantity * TAX_RATE : 0;
    }

    public boolean shouldPayTax(double totalOperationCost) {
        return totalOperationCost > 20000.00;
    }
}
