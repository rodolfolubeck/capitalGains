package org.example.domain.operation;

import org.example.services.portfolio.PortfolioService;

public interface Operation {
    void execute(PortfolioService portfolioService);
    String getType();

}
