package com.example.demo.repo;

import com.example.demo.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    List<User> findByMainTeamNameContainingIgnoreCase(String teamName);
}
