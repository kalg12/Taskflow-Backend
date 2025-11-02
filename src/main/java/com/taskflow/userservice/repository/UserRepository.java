package com.taskflow.userservice.repository;

import com.taskflow.userservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * English: Spring Data repository for User entity. Spring will provide the
 * implementation at runtime. Use methods like findByEmail(...) to fetch users.
 */
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}
