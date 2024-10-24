package com.example.expenseTracker.models;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DueRequest {
    private BigDecimal amount;
    private Integer duePaymentStrategy;
    private String paymentGateway;
}
