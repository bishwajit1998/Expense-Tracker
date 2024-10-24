package com.example.expenseTracker.models;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;

import org.springframework.data.annotation.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Expense {
    @Id
    private String transactionId;
    private String uid;
    private BigDecimal expenseAmount;
    private BigDecimal paidAmount;
    private BigDecimal duePayment;
    private String paymentMethod;
    private String itemName;
    private ExpenseType expenseType;
    private Boolean isSettled;
    private String recipient;

    // @JsonDeserialize(using = LocalDateDeserializer.class)
    // @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDateTime creationTimestamp;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate dueDate;
}
