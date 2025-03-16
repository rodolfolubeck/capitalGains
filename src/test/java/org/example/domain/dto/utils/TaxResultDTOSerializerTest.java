package org.example.domain.dto.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.domain.dto.TaxResultDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TaxResultDTOSerializerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testSerializeZeroTax() throws JsonProcessingException {
        TaxResultDTO taxResultDTO = new TaxResultDTO(0.00);

        String json = objectMapper.writeValueAsString(taxResultDTO);

        String expectedJson = "{\"tax\": 0.00}";

        assertEquals(expectedJson, json);
    }

    @Test
    public void testSerializeNonZeroTax() throws JsonProcessingException {
        TaxResultDTO taxResultDTO = new TaxResultDTO(10.25);

        String json = objectMapper.writeValueAsString(taxResultDTO);

        String expectedJson = "{\"tax\": 10.25}";

        assertEquals(expectedJson, json);
    }

    @Test
    public void testSerializeTaxWithTrailingZeros() throws JsonProcessingException {
        TaxResultDTO taxResultDTO = new TaxResultDTO(5.00);

        String json = objectMapper.writeValueAsString(taxResultDTO);

        String expectedJson = "{\"tax\": 5.00}";

        assertEquals(expectedJson, json);
    }

    @Test
    public void testSerializeNegativeTax() throws JsonProcessingException {
        TaxResultDTO taxResultDTO = new TaxResultDTO(-2.75);

        String json = objectMapper.writeValueAsString(taxResultDTO);

        String expectedJson = "{\"tax\": -2.75}";

        assertEquals(expectedJson, json);
    }

    @Test
    public void testSerializeMultipleTaxes() throws JsonProcessingException {
        TaxResultDTO[] taxResults = {
                new TaxResultDTO(0.00),
                new TaxResultDTO(10.25),
                new TaxResultDTO(5.00)
        };

        String json = objectMapper.writeValueAsString(taxResults);

        String expectedJson = "[{\"tax\": 0.00},{\"tax\": 10.25},{\"tax\": 5.00}]";

        assertEquals(expectedJson, json);
    }
}
