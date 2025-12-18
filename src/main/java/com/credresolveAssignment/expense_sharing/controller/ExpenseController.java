package com.credresolveAssignment.expense_sharing.controller;

import com.credresolveAssignment.expense_sharing.dto.AddExpenseRequest;
import com.credresolveAssignment.expense_sharing.entity.ExpenseEntity;
import com.credresolveAssignment.expense_sharing.service.ExpenseService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @PostMapping
    public ExpenseEntity addExpense(@RequestBody AddExpenseRequest request) {
        return expenseService.addExpense(request);
    }
}
