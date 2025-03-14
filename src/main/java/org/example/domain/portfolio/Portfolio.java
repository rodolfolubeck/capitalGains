package org.example.domain.portfolio;

import org.example.domain.operation.BuyOperation;
import org.example.domain.operation.SellOperation;

import java.util.LinkedList;
import java.util.Queue;

public class Portfolio {
    private final Queue<BuyOperation> buyOperations = new LinkedList<>();
    private double carriedLoss = 0;

    public void addBuy(BuyOperation buy) {
        buyOperations.add(buy);
    }

    public double processSell(SellOperation sell) {
        int quantityToSell = sell.quantity();
        double totalCost = 0.0;
        int quantitySold = 0;
        double profitOrLoss = 0.0;

        while (quantityToSell > 0 && !buyOperations.isEmpty()) {
            BuyOperation buy = buyOperations.peek();
            int availableQuantity = buy.quantity();

            if (availableQuantity <= quantityToSell) {
                buyOperations.poll();
                quantityToSell -= availableQuantity;
                totalCost += availableQuantity * buy.unitCost();
                quantitySold += availableQuantity;
            } else {
                buyOperations.poll();
                buyOperations.add(new BuyOperation(buy.unitCost(), availableQuantity - quantityToSell));
                totalCost += quantityToSell * buy.unitCost();
                quantitySold += quantityToSell;
                quantityToSell = 0;
            }
        }

        double avgBuyPrice = totalCost / quantitySold;

        profitOrLoss = (sell.unitCost() - avgBuyPrice) * quantitySold;

        if (profitOrLoss < 0) {
            carriedLoss += profitOrLoss;
            profitOrLoss = 0;
        } else if (carriedLoss < 0) {
            double remainingLoss = Math.abs(carriedLoss);
            if (remainingLoss >= profitOrLoss) {
                carriedLoss += profitOrLoss;
                profitOrLoss = 0;
            } else {
                carriedLoss = 0;
                profitOrLoss -= remainingLoss;
            }
        }

        double tax = 0;
        if (profitOrLoss > 0 && (sell.unitCost() * sell.quantity()) > 20000.00) {
            tax = profitOrLoss * 0.20;
        }

        return tax;
    }
}
