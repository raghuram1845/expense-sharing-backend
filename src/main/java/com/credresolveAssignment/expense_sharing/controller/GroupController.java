package com.credresolveAssignment.expense_sharing.controller;
import com.credresolveAssignment.expense_sharing.dto.SettlementResponse;

import com.credresolveAssignment.expense_sharing.entity.AppUser;
import com.credresolveAssignment.expense_sharing.entity.ExpenseEntity;
import com.credresolveAssignment.expense_sharing.entity.ExpenseSplitEntity;
import com.credresolveAssignment.expense_sharing.entity.GroupEntity;
import com.credresolveAssignment.expense_sharing.repository.ExpenseRepository;
import com.credresolveAssignment.expense_sharing.repository.GroupRepository;
import com.credresolveAssignment.expense_sharing.repository.UserRepository;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/groups")
public class GroupController {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final ExpenseRepository expenseRepository;

    public GroupController(GroupRepository groupRepository,
                           UserRepository userRepository,
                           ExpenseRepository expenseRepository) {
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
        this.expenseRepository = expenseRepository;
    }
    @GetMapping
    public List<GroupEntity> getAllGroups() {
        return groupRepository.findAll();
    }

    @PostMapping
    public GroupEntity createGroup(@RequestParam String name) {
        GroupEntity group = new GroupEntity();
        group.setName(name);
        return groupRepository.save(group);
    }

    @PostMapping("/{groupId}/addUser/{userId}")
    public GroupEntity addUserToGroup(@PathVariable Long groupId, @PathVariable Long userId) {

        GroupEntity group = groupRepository.findById(groupId).orElseThrow();
        AppUser user = userRepository.findById(userId).orElseThrow();

        group.getMembers().add(user);
        return groupRepository.save(group);
    }
    @GetMapping("/{groupId}")
    public GroupEntity getGroupById(@PathVariable Long groupId) {
        return groupRepository.findById(groupId).orElseThrow(() -> new RuntimeException("Group not found"));
    }

    @GetMapping("/{groupId}/balances")
    public Map<String, Double> getGroupBalances(@PathVariable Long groupId) {

        GroupEntity group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        Map<Long, Double> balances = new HashMap<>();

        // Initialize balances for all group members
        if (group.getMembers() != null) {
            for (AppUser user : group.getMembers()) {
                balances.put(user.getId(), 0.0);
            }
        }

        // Fetch all expenses
        List<ExpenseEntity> expenses = expenseRepository.findAll();

        for (ExpenseEntity expense : expenses) {

            if (expense.getGroup() == null ||
                    !expense.getGroup().getId().equals(groupId)) {
                continue;
            }

            // Add full amount to payer
            AppUser payer = expense.getPaidBy();
            if (payer != null) {
                balances.put(
                        payer.getId(),
                        balances.getOrDefault(payer.getId(), 0.0) + expense.getAmount()
                );
            }

            // Subtract owed amounts
            if (expense.getSplits() != null) {
                for (ExpenseSplitEntity split : expense.getSplits()) {
                    AppUser user = split.getUser();
                    if (user != null) {
                        balances.put(
                                user.getId(),
                                balances.getOrDefault(user.getId(), 0.0) - split.getAmountOwed()
                        );
                    }
                }
            }
        }

        // Convert to readable response
        Map<String, Double> response = new HashMap<>();
        for (AppUser user : group.getMembers()) {
            response.put(user.getName(), balances.getOrDefault(user.getId(), 0.0));
        }

        return response;
    }

    @GetMapping("/{groupId}/settlements")
    public List<SettlementResponse> getSettlements(@PathVariable Long groupId) {

        // Reuse balance logic
        Map<String, Double> balances = getGroupBalances(groupId);

        List<SettlementResponse> settlements = new ArrayList<>();

        List<Map.Entry<String, Double>> creditors = new ArrayList<>();
        List<Map.Entry<String, Double>> debtors = new ArrayList<>();

        // Separate creditors & debtors
        for (Map.Entry<String, Double> entry : balances.entrySet()) {
            if (entry.getValue() > 0) {
                creditors.add(entry);
            } else if (entry.getValue() < 0) {
                debtors.add(entry);
            }
        }

        int i = 0, j = 0;

        // Greedy two-pointer algorithm
        while (i < debtors.size() && j < creditors.size()) {

            Map.Entry<String, Double> debtor = debtors.get(i);
            Map.Entry<String, Double> creditor = creditors.get(j);

            double settleAmount = Math.min(
                    -debtor.getValue(),
                    creditor.getValue()
            );

            settlements.add(
                    new SettlementResponse(
                            debtor.getKey(),
                            creditor.getKey(),
                            settleAmount
                    )
            );

            debtor.setValue(debtor.getValue() + settleAmount);
            creditor.setValue(creditor.getValue() - settleAmount);

            if (debtor.getValue() == 0) i++;
            if (creditor.getValue() == 0) j++;
        }

        return settlements;
    }


}
