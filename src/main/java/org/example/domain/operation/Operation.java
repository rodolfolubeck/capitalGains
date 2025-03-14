package org.example.domain.operation;

import org.example.domain.portfolio.Portfolio;

public interface Operation {
    void execute(Portfolio portfolio);
    String getType();

}
