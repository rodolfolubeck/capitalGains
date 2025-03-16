package org.example.domain.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TaxResultDTOTest {

    @Test
    public void testTaxResultDTO() {
        double expectedTax = 100.50;

        TaxResultDTO taxResultDTO = new TaxResultDTO(expectedTax);

        assertEquals(expectedTax, taxResultDTO.getTax(), "O valor do imposto não é o esperado");
    }
}
