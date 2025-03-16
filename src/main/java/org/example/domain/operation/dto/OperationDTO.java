package org.example.domain.operation.dto;

import org.example.domain.operation.BuyOperation;
import org.example.domain.operation.Operation;
import org.example.domain.operation.SellOperation;

public class OperationDTO {
    private String operation;
    private double unitCost;
    private int quantity;

    public String getOperation() {
        return operation;
    }

    public double getUnitCost() {
        return unitCost;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public void setUnitCost(double unitCost) {
        this.unitCost = unitCost;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }


    public Operation toOperation() {
        return switch (this.operation) {
            case "buy" -> new BuyOperation(unitCost, quantity);
            case "sell" -> new SellOperation(unitCost, quantity);
            default -> throw new IllegalArgumentException("Operação desconhecida: " + operation);
        };
    }
}
