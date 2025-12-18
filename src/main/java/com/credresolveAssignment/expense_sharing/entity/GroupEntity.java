package com.credresolveAssignment.expense_sharing.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class GroupEntity {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @ManyToMany
    private List<AppUser> members =new ArrayList<>();
}
