package com.credresolveAssignment.expense_sharing.repository;

import com.credresolveAssignment.expense_sharing.entity.ExpenseSplitEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenseSplitRepository extends JpaRepository<ExpenseSplitEntity, Long> {
}
