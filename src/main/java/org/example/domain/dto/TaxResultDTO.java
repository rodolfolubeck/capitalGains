package org.example.domain.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.example.domain.dto.utils.TaxResultDTOSerializer;

public class TaxResultDTO {

    @JsonSerialize(using = TaxResultDTOSerializer.class)
    private final double tax;

    public TaxResultDTO(double tax) {
        this.tax = tax;
    }

    public double getTax() {
        return tax;
    }
}
