package com.credresolveAssignment.expense_sharing.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
public class AppUser {

    @Id
    @GeneratedValue
    private Long id;
    
    @Column(unique=true , nullable=false)
    private String name;

    @Column(nullable=false)
    private String email;
}
