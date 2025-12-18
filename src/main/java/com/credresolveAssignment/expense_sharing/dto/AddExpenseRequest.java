package com.credresolveAssignment.expense_sharing.dto;

import com.credresolveAssignment.expense_sharing.split.SplitType;
import java.util.List;
import java.util.Map;

public class AddExpenseRequest {

    private String description;
    private double amount;
    private Long paidByUserId;
    private Long groupId;

    // used for EQUAL split
    private List<Long> participantUserIds;

    // used for EXACT split (userId -> amount)
    private Map<Long, Double> exactAmounts;

    // used for PERCENTAGE split (userId -> percentage)
    private Map<Long, Double> percentageSplits;

    // tells backend which logic to apply
    private SplitType splitType;

    // ===== getters & setters =====

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Long getPaidByUserId() {
        return paidByUserId;
    }

    public void setPaidByUserId(Long paidByUserId) {
        this.paidByUserId = paidByUserId;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public List<Long> getParticipantUserIds() {
        return participantUserIds;
    }

    public void setParticipantUserIds(List<Long> participantUserIds) {
        this.participantUserIds = participantUserIds;
    }

    public Map<Long, Double> getExactAmounts() {
        return exactAmounts;
    }

    public void setExactAmounts(Map<Long, Double> exactAmounts) {
        this.exactAmounts = exactAmounts;
    }

    public Map<Long, Double> getPercentageSplits() {
        return percentageSplits;
    }

    public void setPercentageSplits(Map<Long, Double> percentageSplits) {
        this.percentageSplits = percentageSplits;
    }

    public SplitType getSplitType() {
        return splitType;
    }

    public void setSplitType(SplitType splitType) {
        this.splitType = splitType;
    }
}
