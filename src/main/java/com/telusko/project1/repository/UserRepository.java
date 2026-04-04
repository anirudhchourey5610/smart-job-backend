package com.telusko.project1.repository;

import com.telusko.project1.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {
    User findFirstByEmail(String email);
}
