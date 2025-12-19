package com.credresolveAssignment.expense_sharing.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping
    public String health(){
        return "Live Deployed Backend on Render with PostgreSQL :Expense sharing backend is running";
    }
}
