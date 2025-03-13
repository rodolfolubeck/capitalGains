package org.example.domain;

public record BuyOperation(double unitCost, int quantity) implements Operation {

    @Override
    public void execute() {
        System.out.println("Compra de " + quantity + " unidades a " + unitCost + " cada.");
    }
}
