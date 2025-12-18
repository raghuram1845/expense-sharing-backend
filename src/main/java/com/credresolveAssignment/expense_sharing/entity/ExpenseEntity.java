package com.credresolveAssignment.expense_sharing.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class ExpenseEntity {

    @Id
    @GeneratedValue
    private Long id;

    private String description;

    private double amount;

    @ManyToOne
    private AppUser paidBy;

    @ManyToOne
    private GroupEntity group;

    @OneToMany(mappedBy = "expense", cascade = CascadeType.ALL)
    private List<ExpenseSplitEntity> splits=new ArrayList<>();;
}
