package org.example.domain.dto;

public class TaxDTO {
    private double tax;

    public TaxDTO(double tax) {
        this.tax = tax;
    }

    public double getTax() {
        return tax;
    }

    public void setTax(double tax) {
        this.tax = tax;
    }
}
