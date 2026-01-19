package com.solocoffee.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryDTO {
    private Long id;
    private Long productId;
    private String productName;
    private String productNo;
    private BigDecimal quantity;
    private String unit;
    private BigDecimal warningThreshold;
}
