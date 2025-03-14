package org.example.domain.operation;

import org.example.domain.portfolio.Portfolio;

public record BuyOperation(double unitCost, int quantity) implements Operation {

    @Override
    public void execute(Portfolio portfolio) {
        portfolio.addBuy(this);
    }

    @Override
    public String getType() {
        return "buy";
    }
}
