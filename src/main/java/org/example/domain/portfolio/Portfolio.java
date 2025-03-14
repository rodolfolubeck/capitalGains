package org.example.domain.portfolio;

import org.example.domain.operation.BuyOperation;
import org.example.domain.operation.SellOperation;
import org.example.service.TaxCalculatorService;

import java.util.LinkedList;
import java.util.Queue;

public class Portfolio {
    private final Queue<BuyOperation> buyOperations = new LinkedList<>();
    private double carriedLoss = 0;  // Armazenar prejuízo a ser deduzido de lucros futuros

    public void addBuy(BuyOperation buy) {
        buyOperations.add(buy);
    }

    public double processSell(SellOperation sell) {
        int quantityToSell = sell.quantity();
        double totalCost = 0.0;
        int quantitySold = 0;
        double profitOrLoss = 0.0; // Para acumular o lucro ou prejuízo da operação

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

        // Calcular preço médio ponderado
        double avgBuyPrice = totalCost / quantitySold;

        // Verificar se há lucro ou prejuízo
        profitOrLoss = (sell.unitCost() - avgBuyPrice) * quantitySold;

        // Deduzir prejuízo dos lucros futuros, se houver
        if (profitOrLoss < 0) {
            carriedLoss += profitOrLoss;  // Se houver prejuízo, acumula
            profitOrLoss = 0;  // Não paga imposto sobre prejuízo
        } else if (carriedLoss < 0) {
            // Se houver prejuízo acumulado, deduzir do lucro
            double remainingLoss = Math.abs(carriedLoss);
            if (remainingLoss >= profitOrLoss) {
                carriedLoss += profitOrLoss;  // Deduzir todo o lucro
                profitOrLoss = 0;  // Não paga imposto sobre lucro total
            } else {
                carriedLoss = 0;  // Prejuízo foi completamente deduzido
                profitOrLoss -= remainingLoss;  // Deduz o prejuízo acumulado
            }
        }

        // Calcular o imposto: 20% sobre o lucro
        double tax = 0;
        if (profitOrLoss > 0 && (sell.unitCost() * sell.quantity()) > 20000.00) {
            tax = profitOrLoss * 0.20;  // Imposto de 20% sobre o lucro
        }

        return tax;
    }
}
