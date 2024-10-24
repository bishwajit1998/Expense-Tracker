package com.example.expenseTracker.models;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonIgnoreProperties
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "expenses")
public class ExpenseRequest {
    @Id
    private String expenseId;
    private String uid;
    private String itemName;
    private BigDecimal expenseAmount;
    private BigDecimal paidAmount;
    private BigDecimal duePayment;
    private String paymentMethod;
    private LocalDateTime creationTimestamp;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate dueDate;
    private Boolean isSettled;
    private ExpenseType expenseType;
    private String recipient;

    /*
     * Setting up a constructor to make a copy of an existing expense request
     * object.
     * Used while setting dues.
     */
    public ExpenseRequest(ExpenseRequest expenseRequest) {
        this.uid = expenseRequest.getUid();
        this.recipient = expenseRequest.getRecipient();
        this.itemName = expenseRequest.getItemName();
        this.expenseAmount = expenseRequest.getExpenseAmount();
        this.paidAmount = expenseRequest.getPaidAmount();
        this.duePayment = expenseRequest.getDuePayment();
        this.paymentMethod = expenseRequest.getPaymentMethod();
        this.creationTimestamp = expenseRequest.getCreationTimestamp();
        this.dueDate = expenseRequest.getDueDate();
        this.isSettled = expenseRequest.getIsSettled();
        this.expenseType = expenseRequest.getExpenseType();
    }
}
