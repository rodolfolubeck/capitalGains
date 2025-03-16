package org.example.domain.portfolio;

import org.example.domain.operation.BuyOperation;
import org.example.domain.operation.SellOperation;

import java.util.LinkedList;
import java.util.Queue;

public class Portfolio {
    private final Queue<BuyOperation> buyOperations = new LinkedList<>();
    private double carriedLoss = 0.0;
    private double weightedAveragePrice = 0.0;
    private int totalQuantity = 0;

    public void addBuy(BuyOperation buy) {
        // Recalcula o preço médio ponderado
        weightedAveragePrice = ((totalQuantity * weightedAveragePrice) + (buy.quantity() * buy.unitCost()))
                / (totalQuantity + buy.quantity());

        totalQuantity += buy.quantity();
        buyOperations.add(buy);
    }

    public double processSell(SellOperation sell) {
        int quantityToSell = sell.quantity();
        double totalSellValue = sell.unitCost() * quantityToSell;
        double profitOrLoss = (sell.unitCost() - weightedAveragePrice) * quantityToSell;

        totalQuantity -= quantityToSell;
        removeOldBuys(quantityToSell);

        // Dedução correta do prejuízo acumulado
        profitOrLoss = applyCarriedLoss(profitOrLoss);

        // Calcula imposto se necessário
        return calculateTax(profitOrLoss, totalSellValue);
    }

    private void removeOldBuys(int quantityToSell) {
        while (quantityToSell > 0 && !buyOperations.isEmpty()) {
            BuyOperation buy = buyOperations.poll();
            int availableQuantity = buy.quantity();

            if (availableQuantity > quantityToSell) {
                buyOperations.add(new BuyOperation(buy.unitCost(), availableQuantity - quantityToSell));
                return;
            }

            quantityToSell -= availableQuantity;
        }
    }

    private double applyCarriedLoss(double profitOrLoss) {
        if (profitOrLoss < 0) {
            carriedLoss += profitOrLoss; // Acumula prejuízo negativo
            return 0;
        }

        if (carriedLoss < 0) {
            double remainingLoss = Math.abs(carriedLoss);
            if (remainingLoss >= profitOrLoss) {
                carriedLoss += profitOrLoss;
                return 0;
            }
            carriedLoss = 0;
            return profitOrLoss - remainingLoss;
        }

        return profitOrLoss;
    }

    private double calculateTax(double profitOrLoss, double totalSellValue) {
        if (profitOrLoss > 0 && totalSellValue > 20000.00) {
            return profitOrLoss * 0.20;
        }
        return 0;
    }
}
