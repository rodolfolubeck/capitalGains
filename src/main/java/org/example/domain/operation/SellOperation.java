package org.example.domain.operation;

import org.example.services.portfolio.PortfolioService;

public record SellOperation(double unitCost, int quantity) implements Operation {

    @Override
    public void execute(PortfolioService portfolioService) {
        double tax = portfolioService.processSell(this);
    }

    @Override
    public String getType() {
        return "sell";
    }
}
