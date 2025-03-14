package org.example.domain.operation;

import org.example.domain.portfolio.Portfolio;

public record SellOperation(double unitCost, int quantity) implements Operation {

    @Override
    public void execute(Portfolio portfolio) {
        double tax = portfolio.processSell(this);
    }

    @Override
    public String getType() {
        return "sell";
    }
}
