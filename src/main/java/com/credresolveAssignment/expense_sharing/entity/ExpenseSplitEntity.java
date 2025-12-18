package com.credresolveAssignment.expense_sharing.entity;

import  jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class ExpenseSplitEntity {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private ExpenseEntity expense;

    @ManyToOne
    private AppUser user;

    private double amountOwed;
}
