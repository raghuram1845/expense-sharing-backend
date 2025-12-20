package com.credresolveAssignment.expense_sharing.dto;

import java.util.List;
import java.util.Map;

public class AddExpenseRequest {

    private String description;
    private double amount;
    private Long paidByUserId;
    private Long groupId;

    // NEW
    private String splitType; // EQUAL, EXACT, PERCENTAGE

    // For EQUAL
    private List<Long> participantUserIds;

    // For EXACT
    private Map<Long, Double> exactAmounts;

    // For PERCENTAGE
    private Map<Long, Double> percentages;

    // getters & setters
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public Long getPaidByUserId() { return paidByUserId; }
    public void setPaidByUserId(Long paidByUserId) { this.paidByUserId = paidByUserId; }

    public Long getGroupId() { return groupId; }
    public void setGroupId(Long groupId) { this.groupId = groupId; }

    public String getSplitType() { return splitType; }
    public void setSplitType(String splitType) { this.splitType = splitType; }

    public List<Long> getParticipantUserIds() { return participantUserIds; }
    public void setParticipantUserIds(List<Long> participantUserIds) {
        this.participantUserIds = participantUserIds;
    }

    public Map<Long, Double> getExactAmounts() { return exactAmounts; }
    public void setExactAmounts(Map<Long, Double> exactAmounts) {
        this.exactAmounts = exactAmounts;
    }

    public Map<Long, Double> getPercentages() { return percentages; }
    public void setPercentages(Map<Long, Double> percentages) {
        this.percentages = percentages;
    }
}
