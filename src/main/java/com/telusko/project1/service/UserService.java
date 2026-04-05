package com.telusko.project1.service;

import com.telusko.project1.model.User;
import com.telusko.project1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User registerUser(User user) {
        if (user.getEmail() == null || user.getPassword() == null) {
            throw new RuntimeException("Email and Password are required.");
        }
        String lowerEmail = user.getEmail().toLowerCase();
        
        // Prevent duplicate registrations from crashing the database
        if (userRepository.findFirstByEmail(lowerEmail) != null) {
            System.err.println("Registration blocked: Email already exists - " + lowerEmail);
            throw new RuntimeException("Email already signed up.");
        }
        user.setEmail(lowerEmail);
        
        // Hash the password before storing it into MySQL.
        // It will convert "password123" into an uncrackable 60-character string like $2a$10$xyz...
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        System.out.println("Registering user securely: " + user.getEmail());
        return userRepository.save(user);
    }
    public User loginUser(String email, String password) {
        if (email == null || password == null) {
            System.err.println("Login failed: Email or password is null");
            return null;
        }
        
        String lowerEmail = email.toLowerCase();
        System.out.println("Attempting login for: " + lowerEmail);
        User user = userRepository.findFirstByEmail(lowerEmail);
        
        if (user == null) {
            System.err.println("Login failed: No user found with email " + lowerEmail);
            return null;
        }
        
        // Compare the raw password from the frontend against the hashed cipher from MySQL.
        if (passwordEncoder.matches(password, user.getPassword())) {
            System.out.println("Login success for: " + lowerEmail);
            return user;
        } else {
            System.err.println("Login failed: Password mismatch for " + lowerEmail);
            return null;
        }
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
