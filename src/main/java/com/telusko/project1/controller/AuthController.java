package com.telusko.project1.controller;

import com.telusko.project1.dto.LoginRequest;
import com.telusko.project1.model.User;
import com.telusko.project1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public User register(@RequestBody User user) {
        return userService.registerUser(user);
    }
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest loginRequest) {
        System.out.println("Login request received for: " + loginRequest.getEmail());
        User loggedInUser = userService.loginUser(loginRequest.getEmail(), loginRequest.getPassword());
        Map<String, String> response = new HashMap<>();
        if (loggedInUser != null) {
            response.put("message", "Login Successful!");
            response.put("token", "dummy-jwt-token-for-now"); 
            response.put("username", loggedInUser.getName());
            return ResponseEntity.ok(response);
        } else {
            response.put("error", "Invalid email or password! Please try again.");
            return ResponseEntity.status(401).body(response);
        }
    }

    @GetMapping("/users")
    public List<User> getAllUsers() {
        System.out.println("Fetching all users for debugging...");
        return userService.getAllUsers();
    }

}
