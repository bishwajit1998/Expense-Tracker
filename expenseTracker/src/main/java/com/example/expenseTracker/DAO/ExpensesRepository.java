package com.example.expenseTracker.dao;

import java.util.List;

import com.example.expenseTracker.models.Expense;
import com.example.expenseTracker.models.ExpenseRequest;
import com.example.expenseTracker.models.ExpenseType;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Component;

@Component
public interface ExpensesRepository extends MongoRepository<ExpenseRequest, String> {

    @Query("{uid : ?0, expenseType:?1}")
    List<Expense> findByUidAndExpense(String uid, ExpenseType expenseType);

    @Query("{uid : ?0, isSettled: ?1}")
    List<Expense> findByUidAndIsSettled(String uid, Boolean isSettled);
}