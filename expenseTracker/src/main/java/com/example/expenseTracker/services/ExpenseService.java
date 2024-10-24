package com.example.expenseTracker.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import com.example.expenseTracker.dao.ExpensesRepository;
import com.example.expenseTracker.dao.UserRepository;
import com.example.expenseTracker.models.Due;
import com.example.expenseTracker.models.Expense;
import com.example.expenseTracker.models.ExpenseRequest;
import com.example.expenseTracker.models.ExpenseType;
import com.example.expenseTracker.models.User;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ExpenseService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExpensesRepository expensesRepository;

    /*
     * Purpose: Get the user in DB by their email Address
     * input: Email address
     * output: User
     */
    private User getUser(String userEmailAddress) {
        return userRepository.findByEmailAddress(userEmailAddress);
    }

    /*
     * Purpose: Register the user if not already present in the DB
     * input: User
     * output:
     * true : if User is successfully registered
     * false: if user is already present.
     */
    public Boolean registerUser(User user) {
        if (!Objects.nonNull(getUser(user.getEmailAddress()))) {
            userRepository.save(user);
            return true;
        }

        else {
            return false;
        }
    }

    /*
     * Purpose: Login the incoming user if present in DB
     * input: email Address
     * output: User
     */
    public User login(String emailAddress) {

        User user = getUser(emailAddress);
        if (Objects.nonNull(user)) {
            return user;
        } else {
            return null;
        }
    }

    /*
     * Purpose: Setting the flag according to due remaining for this expense
     * if paidAmount < actual Amount
     * isSettled = false
     * else
     * isSettled = true
     */
    private void setupForDue(ExpenseRequest expenseRequest, Boolean isSettled) {
        expenseRequest.setIsSettled(isSettled);
    }

    /*
     * Purpose: Recording an Expense Only transaction in DB.
     */
    public void recordTransaction(ExpenseRequest expenseRequest) {
        BigDecimal expense = expenseRequest.getExpenseAmount();
        BigDecimal paidAmount = expenseRequest.getPaidAmount();
        BigDecimal due = expense.subtract(paidAmount);
        expenseRequest.setDuePayment(due);
        expenseRequest.setCreationTimestamp(LocalDateTime.now());
        // The expense is not paid in full
        if (due.doubleValue() > 0.0) {
            setupForDue(expenseRequest, false);
        } else {
            setupForDue(expenseRequest, true);
        }

        expensesRepository.save(expenseRequest);
    }

    /*
     * Fetches list of all Expenses (excluding due settle requests) of the user
     */
    public List<Expense> getUserExpenses(String uid) {
        List<Expense> usertransactions = expensesRepository.findByUidAndExpense(uid, ExpenseType.EXPENSE);
        return usertransactions;
    }

    /*
     * Purpose: Fetches list of all dues remaining for the user
     */
    public List<Due> getAllDues(String uid) {
        List<Expense> unsettledTransactions = expensesRepository.findByUidAndIsSettled(uid, false);
        ModelMapper modelMapper = new ModelMapper();
        List<Due> usertransactions = unsettledTransactions.stream()
                .map(unsettledTransaction -> modelMapper.map(unsettledTransaction,
                        Due.class))
                .collect(Collectors.toList());
        return usertransactions;
    }

    /*
     * Purpose: Handles due settling requests for the user
     * input: Amount,
     */
    private void setupAndInsertDuePaidRequest(ExpenseRequest expenseRequest, BigDecimal duePayment,
            String paymentGateway) {
        // Making the record of this due settlement request
        ExpenseRequest duePaidRequest = new ExpenseRequest(expenseRequest);
        duePaidRequest.setExpenseAmount(duePayment);
        duePaidRequest.setPaidAmount(duePayment);
        duePaidRequest.setExpenseType(ExpenseType.DUE);
        duePaidRequest.setPaymentMethod(paymentGateway);
        duePaidRequest.setIsSettled(true);
        duePaidRequest.setDuePayment(BigDecimal.valueOf(0.0));
        expensesRepository.save(duePaidRequest);
    }

    /*
     * Purpose: Updates the existing documents in DB to take due settled requests.
     * Impact: Always have updated expense status for every expense.
     */
    private void updateExpense(ExpenseRequest expenseRequest, BigDecimal duePayment, BigDecimal paid) {
        BigDecimal paidAmount = expenseRequest.getPaidAmount();
        expenseRequest.setPaidAmount(paidAmount.add(paid));
        expenseRequest.setDuePayment(duePayment.subtract(paid));
        expensesRepository.save(expenseRequest);
    }

    /*
     * This function settles dues in order of the entry in input list of dues
     * input:
     * userDues: List of dues
     * amount: Total amount provided by user
     * paymentGateway: Which payment gateway
     * 
     * output:
     * Boolean: If due is settled and update the expenses accordingly.
     * 
     */
    private boolean duesSettleInorder(List<Due> userDues, BigDecimal amount, String paymentGateway) {
        for (Due userDue : userDues) {
            BigDecimal duePayment = userDue.getDuePayment();
            String expenseId = userDue.getExpenseId();
            Optional<ExpenseRequest> expenseRequestOptional = expensesRepository.findById(expenseId);
            /*
             * amount <=0 : break
             * amount>duePayment : pay the due amount in full
             * amount<duePayment : pay whole amount.
             */
            if (amount.doubleValue() <= 0.0)
                break;
            else if (expenseRequestOptional.isPresent()) {
                ExpenseRequest expenseRequest = expenseRequestOptional.get();
                // If remaining amount is more than due amount, pay in full.
                if (amount.compareTo(duePayment) >= 0) {
                    expenseRequest.setIsSettled(true);
                    // pay in full
                    updateExpense(expenseRequest, duePayment, duePayment);
                    setupAndInsertDuePaidRequest(expenseRequest, duePayment, paymentGateway);
                    amount = amount.subtract(duePayment);
                }
                // if smaller, pay maximum
                else if (amount.compareTo(duePayment) < 0) {
                    // pay maximum
                    updateExpense(expenseRequest, duePayment, amount);
                    setupAndInsertDuePaidRequest(expenseRequest, amount, paymentGateway);
                    amount = BigDecimal.valueOf(0.0);
                } else
                    break;
            } else {
                return false;
            }
        }
        return true;

    }

    /*
     * method
     * 1: First in first out - the due added first will be paid first
     * 2: Latest repayment date first - the due that is nearest will be paid first
     * 
     * output: Returns list of updated dues.
     */
    public List<Due> settleDues(String uid, BigDecimal amount, Integer duePaymentStrategy, String paymentGateway) {
        // Getting all the unsettled transactions for a given user.
        List<Due> userDues = getAllDues(uid);
        Boolean dueSettled = false;
        switch (duePaymentStrategy) {
            case 1:
                // FIFO
                dueSettled = duesSettleInorder(userDues, amount, paymentGateway);
                break;
            case 2:
                // Latest repayment date first - the due that is nearest will be paid first
                Collections.sort(userDues, Due.DueDateComparator);
                dueSettled = duesSettleInorder(userDues, amount, paymentGateway);
                break;
        }
        if (dueSettled) {
            return getAllDues(uid);
        } else
            return null;
    }

    /*
     * Purpose: Returns history of all due settling requests in reverse(latest
     * settle request first)
     * Input: user id, ExpeneType
     * Output: List of expense which were due settling requests.
     */
    public List<Expense> getAllSettledDues(String uid) {
        List<Expense> duesSettlingRequestsHistory = expensesRepository.findByUidAndExpense(uid, ExpenseType.DUE);
        Collections.reverse(duesSettlingRequestsHistory);
        return duesSettlingRequestsHistory;
    }
}
