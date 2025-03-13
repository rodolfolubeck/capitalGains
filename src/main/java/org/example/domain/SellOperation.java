package org.example.domain;

public record SellOperation(double unitCost, int quantity) implements Operation {

    @Override
    public void execute() {
        System.out.println("Venda de " + quantity + " unidades a " + unitCost + " cada.");
    }
}
