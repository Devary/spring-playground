package com.example.demo.service;

import com.example.demo.entity.Team;
import com.example.demo.entity.User;
import com.example.demo.repo.TeamRepository;
import com.example.demo.repo.UserRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final TeamRepository teamRepository;

    @Transactional
    public User createUser(User user) {
        log.info("Creating user with email {}", user.getEmail());
        user.setLeadTeam(resolveTeam(user.getLeadTeam()));
        user.setMainTeam(resolveTeam(user.getMainTeam()));
        if (user.getSupportingTeams() != null) {
            var resolvedTeams = user.getSupportingTeams().stream().map(this::resolveTeam).toList();
            user.getSupportingTeams().clear();
            resolvedTeams.forEach(user::addSupportingTeam);
        }
        return userRepository.save(user);
    }

    @Transactional
    public Optional<User> updateUser(Long id, User user) {
        return userRepository.findById(id).map(existing -> {
            log.info("Updating user {}", id);
            existing.setUsername(user.getUsername());
            existing.setEmail(user.getEmail());
            existing.setPassword(user.getPassword());
            existing.setLeadTeam(resolveTeam(user.getLeadTeam()));
            existing.setMainTeam(resolveTeam(user.getMainTeam()));
            existing.getSupportingTeams().clear();
            if (user.getSupportingTeams() != null) {
                user.getSupportingTeams().stream().map(this::resolveTeam).forEach(existing::addSupportingTeam);
            }
            return existing;
        });
    }

    @Transactional
    public Optional<User> partialUpdate(Long id, String email) {
        return userRepository.findById(id).map(existing -> {
            log.info("Partially updating user {} email to {}", id, email);
            existing.setEmail(email);
            return existing;
        });
    }

    @Transactional
    public void deleteUser(Long id) {
        log.warn("Deleting user {}", id);
        userRepository.deleteById(id);
    }

    @Transactional
    public Optional<User> assignLeadTeam(Long userId, UUID teamId) {
        return userRepository.findById(userId).flatMap(user -> teamRepository.findById(teamId).map(team -> {
            log.info("Assigning team {} as lead for user {}", teamId, userId);
            user.setLeadTeam(team);
            return user;
        }));
    }

    @Transactional
    public Optional<User> findByEmail(String email) {
        log.debug("Searching for user by email {}", email);
        return userRepository.findByEmail(email);
    }

    @Transactional
    public List<User> findByTeamName(String teamName) {
        log.debug("Finding users by team name like {}", teamName);
        return userRepository.findByMainTeamNameContainingIgnoreCase(teamName);
    }

    @Transactional
    public Optional<User> findById(Long id) {
        log.debug("Finding user by id {}", id);
        return userRepository.findById(id);
    }

    @Transactional
    public List<User> findAll() {
        log.debug("Retrieving all users");
        return userRepository.findAll();
    }

    private Team resolveTeam(Team candidate) {
        if (candidate == null || candidate.getId() == null) {
            return candidate;
        }
        return teamRepository.findById(candidate.getId()).orElse(candidate);
    }
}
