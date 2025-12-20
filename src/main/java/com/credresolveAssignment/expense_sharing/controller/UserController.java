package com.credresolveAssignment.expense_sharing.controller;

import com.credresolveAssignment.expense_sharing.entity.AppUser;
import com.credresolveAssignment.expense_sharing.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserRepository userRepository;

    public UserController(UserRepository userRepository){
        this.userRepository=userRepository;
    }
    @PostMapping
    public AppUser createUser(@RequestBody AppUser User){
        return userRepository.save(User);
    }

    @GetMapping
    public List<AppUser> getAllUsers(){
        return userRepository.findAll();
    }
    @DeleteMapping
    public void deleteAllUsers() {
        userRepository.deleteAll();
    }
}
