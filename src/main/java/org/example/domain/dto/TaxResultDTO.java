package org.example.domain.dto;

public class TaxResultDTO {
    private final double tax;

    public TaxResultDTO(double tax) {
        this.tax = tax;
    }

    public double getTax() {
        return tax;
    }
}
