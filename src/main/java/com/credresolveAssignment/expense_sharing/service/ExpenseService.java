package com.credresolveAssignment.expense_sharing.service;

import com.credresolveAssignment.expense_sharing.dto.AddExpenseRequest;
import com.credresolveAssignment.expense_sharing.entity.AppUser;
import com.credresolveAssignment.expense_sharing.entity.ExpenseEntity;
import com.credresolveAssignment.expense_sharing.entity.ExpenseSplitEntity;
import com.credresolveAssignment.expense_sharing.entity.GroupEntity;
import com.credresolveAssignment.expense_sharing.repository.ExpenseRepository;
import com.credresolveAssignment.expense_sharing.repository.GroupRepository;
import com.credresolveAssignment.expense_sharing.repository.UserRepository;
import com.credresolveAssignment.expense_sharing.split.SplitType;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;

    public ExpenseService(ExpenseRepository expenseRepository,
                          UserRepository userRepository,
                          GroupRepository groupRepository) {
        this.expenseRepository = expenseRepository;
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
    }

    public ExpenseEntity addExpense(AddExpenseRequest request) {

        // Fetch payer and group
        AppUser paidBy = userRepository.findById(request.getPaidByUserId())
                .orElseThrow(() -> new RuntimeException("Payer not found"));

        GroupEntity group = groupRepository.findById(request.getGroupId())
                .orElseThrow(() -> new RuntimeException("Group not found"));

        // Create expense
        ExpenseEntity expense = new ExpenseEntity();
        expense.setDescription(request.getDescription());
        expense.setAmount(request.getAmount());
        expense.setPaidBy(paidBy);
        expense.setGroup(group);

        List<ExpenseSplitEntity> splits = new ArrayList<>();

        // Convert splitType String → Enum
        SplitType splitType = SplitType.valueOf(request.getSplitType());

        // Handle split logic
        if (splitType == SplitType.EQUAL) {
            handleEqualSplit(request, expense, splits);
        } else if (splitType == SplitType.EXACT) {
            handleExactSplit(request, expense, splits);
        } else if (splitType == SplitType.PERCENTAGE) {
            handlePercentageSplit(request, expense, splits);
        } else {
            throw new RuntimeException("Invalid split type");
        }

        // Save expense with splits
        expense.setSplits(splits);
        return expenseRepository.save(expense);
    }

    //EQUAL SPLIT
    private void handleEqualSplit(AddExpenseRequest request,
                                  ExpenseEntity expense,
                                  List<ExpenseSplitEntity> splits) {

        int count = request.getParticipantUserIds().size();
        double perHead = expense.getAmount() / count;

        for (Long userId : request.getParticipantUserIds()) {
            AppUser user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            ExpenseSplitEntity split = new ExpenseSplitEntity();
            split.setExpense(expense);
            split.setUser(user);
            split.setAmountOwed(perHead);

            splits.add(split);
        }
    }

    //EXACT SPLIT
    private void handleExactSplit(AddExpenseRequest request,
                                  ExpenseEntity expense,
                                  List<ExpenseSplitEntity> splits) {

        double total = 0;
        for (double amt : request.getExactAmounts().values()) {
            total += amt;
        }

        // Floating point safe comparison
        if (Math.abs(total - expense.getAmount()) > 0.01) {
            throw new RuntimeException("Exact split total does not match expense amount");
        }

        for (Map.Entry<Long, Double> entry : request.getExactAmounts().entrySet()) {
            AppUser user = userRepository.findById(entry.getKey())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            ExpenseSplitEntity split = new ExpenseSplitEntity();
            split.setExpense(expense);
            split.setUser(user);
            split.setAmountOwed(entry.getValue());

            splits.add(split);
        }
    }

    //PERCENTAGE SPLIT
    private void handlePercentageSplit(AddExpenseRequest request,
                                       ExpenseEntity expense,
                                       List<ExpenseSplitEntity> splits) {

        double percentSum = 0;
        for (double p : request.getPercentages().values()) {
            percentSum += p;
        }

        if (Math.abs(percentSum - 100) > 0.01) {
            throw new RuntimeException("Percentages must sum to 100");
        }

        for (Map.Entry<Long, Double> entry : request.getPercentages().entrySet()) {
            AppUser user = userRepository.findById(entry.getKey())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            double amount = expense.getAmount() * (entry.getValue() / 100);

            ExpenseSplitEntity split = new ExpenseSplitEntity();
            split.setExpense(expense);
            split.setUser(user);
            split.setAmountOwed(amount);

            splits.add(split);
        }
    }
}
