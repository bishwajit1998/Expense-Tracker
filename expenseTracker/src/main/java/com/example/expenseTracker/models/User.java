package com.example.expenseTracker.models;

import java.util.List;

import com.example.expenseTracker.services.ExpenseService;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties
public class User {

    @Id
    private String uid;
    private String emailAddress;
    private String userName;

    private ExpenseService expenseService;

    public List<Expense> getUserExpenses(ExpenseService expenseService) {
        this.expenseService = expenseService;
        List<Expense> usertransactions = expenseService.getUserExpenses(this.getUid());
        return usertransactions;
    }

    public List<Expense> getUserSettledDues(ExpenseService expenseService) {
        this.expenseService = expenseService;
        List<Expense> usertransactions = expenseService.getAllSettledDues(this.getUid());
        return usertransactions;
    }

    public List<Due> getUserDues(ExpenseService expenseService) {
        this.expenseService = expenseService;
        return expenseService.getAllDues(this.getUid());
    }

}
