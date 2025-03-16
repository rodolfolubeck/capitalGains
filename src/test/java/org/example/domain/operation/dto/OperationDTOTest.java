package org.example.domain.operation.dto;

import org.example.domain.operation.BuyOperation;
import org.example.domain.operation.Operation;
import org.example.domain.operation.SellOperation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OperationDTOTest {

    @Test
    public void testGetOperation() {
        OperationDTO operationDTO = new OperationDTO();
        operationDTO.setOperation("buy");
        assertEquals("buy", operationDTO.getOperation(), "A operação não foi retornada corretamente.");
    }

    @Test
    public void testGetUnitCost() {
        OperationDTO operationDTO = new OperationDTO();
        operationDTO.setUnitCost(10.50);
        assertEquals(10.50, operationDTO.getUnitCost(), "O custo unitário não foi retornado corretamente.");
    }

    @Test
    public void testGetQuantity() {
        OperationDTO operationDTO = new OperationDTO();
        operationDTO.setQuantity(100);
        assertEquals(100, operationDTO.getQuantity(), "A quantidade não foi retornada corretamente.");
    }

    @Test
    public void testToOperationBuy() {
        OperationDTO operationDTO = new OperationDTO();
        operationDTO.setOperation("buy");
        operationDTO.setUnitCost(10.00);
        operationDTO.setQuantity(50);

        Operation operation = operationDTO.toOperation();
        assertTrue(operation instanceof BuyOperation, "A operação não foi convertida corretamente para BuyOperation.");
        BuyOperation buyOperation = (BuyOperation) operation;
        assertEquals(10.00, buyOperation.unitCost(), "O custo unitário da compra está incorreto.");
        assertEquals(50, buyOperation.quantity(), "A quantidade de ações na compra está incorreta.");
    }

    @Test
    public void testToOperationSell() {
        OperationDTO operationDTO = new OperationDTO();
        operationDTO.setOperation("sell");
        operationDTO.setUnitCost(15.00);
        operationDTO.setQuantity(30);

        Operation operation = operationDTO.toOperation();
        assertTrue(operation instanceof SellOperation, "A operação não foi convertida corretamente para SellOperation.");
        SellOperation sellOperation = (SellOperation) operation;
        assertEquals(15.00, sellOperation.unitCost(), "O custo unitário da venda está incorreto.");
        assertEquals(30, sellOperation.quantity(), "A quantidade de ações na venda está incorreta.");
    }

    @Test
    public void testToOperationInvalidOperation() {
        OperationDTO operationDTO = new OperationDTO();
        operationDTO.setOperation("invalid");

        assertThrows(IllegalArgumentException.class, operationDTO::toOperation,
                "Deveria lançar IllegalArgumentException para operação inválida.");
    }
}
