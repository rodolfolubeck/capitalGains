package org.example.domain.operation;

import org.example.services.portfolio.PortfolioService;

public record BuyOperation(double unitCost, int quantity) implements Operation {

    @Override
    public void execute(PortfolioService portfolioService) {
        portfolioService.addBuy(this);
    }

    @Override
    public String getType() {
        return "buy";
    }
}
