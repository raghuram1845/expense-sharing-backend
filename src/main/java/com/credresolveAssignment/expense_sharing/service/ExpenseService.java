package com.credresolveAssignment.expense_sharing.service;

import com.credresolveAssignment.expense_sharing.dto.AddExpenseRequest;
import com.credresolveAssignment.expense_sharing.entity.*;
import com.credresolveAssignment.expense_sharing.repository.*;
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

        AppUser paidBy = userRepository.findById(request.getPaidByUserId()).orElseThrow();
        GroupEntity group = groupRepository.findById(request.getGroupId()).orElseThrow();

        ExpenseEntity expense = new ExpenseEntity();
        expense.setDescription(request.getDescription());
        expense.setAmount(request.getAmount());
        expense.setPaidBy(paidBy);
        expense.setGroup(group);

        List<ExpenseSplitEntity> splits = new ArrayList<>();

        if (request.getSplitType() == SplitType.EQUAL) {
            handleEqualSplit(request, expense, splits);
        } else if (request.getSplitType() == SplitType.EXACT) {
            handleExactSplit(request, expense, splits);
        } else if (request.getSplitType() == SplitType.PERCENTAGE) {
            handlePercentageSplit(request, expense, splits);
        } else {
            throw new RuntimeException("Invalid split type");
        }

        expense.setSplits(splits);
        return expenseRepository.save(expense);
    }

    private void handleEqualSplit(AddExpenseRequest request,
                                  ExpenseEntity expense,
                                  List<ExpenseSplitEntity> splits) {

        double perHead = expense.getAmount() / request.getParticipantUserIds().size();

        for (Long userId : request.getParticipantUserIds()) {
            AppUser user = userRepository.findById(userId).orElseThrow();

            ExpenseSplitEntity split = new ExpenseSplitEntity();
            split.setExpense(expense);
            split.setUser(user);
            split.setAmountOwed(perHead);

            splits.add(split);
        }
    }

    private void handleExactSplit(AddExpenseRequest request,
                                  ExpenseEntity expense,
                                  List<ExpenseSplitEntity> splits) {

        double total = 0;
        for (double amt : request.getExactAmounts().values()) {
            total += amt;
        }

        if (total != expense.getAmount()) {
            throw new RuntimeException("Exact split total mismatch");
        }

        for (Map.Entry<Long, Double> entry : request.getExactAmounts().entrySet()) {
            AppUser user = userRepository.findById(entry.getKey()).orElseThrow();

            ExpenseSplitEntity split = new ExpenseSplitEntity();
            split.setExpense(expense);
            split.setUser(user);
            split.setAmountOwed(entry.getValue());

            splits.add(split);
        }
    }

    private void handlePercentageSplit(AddExpenseRequest request,
                                       ExpenseEntity expense,
                                       List<ExpenseSplitEntity> splits) {

        double percentSum = 0;
        for (double p : request.getPercentageSplits().values()) {
            percentSum += p;
        }

        if (percentSum != 100) {
            throw new RuntimeException("Percentages must sum to 100");
        }

        for (Map.Entry<Long, Double> entry : request.getPercentageSplits().entrySet()) {
            AppUser user = userRepository.findById(entry.getKey()).orElseThrow();

            double amount = expense.getAmount() * (entry.getValue() / 100);

            ExpenseSplitEntity split = new ExpenseSplitEntity();
            split.setExpense(expense);
            split.setUser(user);
            split.setAmountOwed(amount);

            splits.add(split);
        }
    }
}
