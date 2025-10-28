package com.example.demo.service;

import com.example.demo.entity.Team;
import com.example.demo.entity.User;
import com.example.demo.repo.TeamRepository;
import com.example.demo.repo.UserRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TeamService {

    @Autowired
    private TeamRepository teamRepository;

    private UserRepository userRepository;

    @Autowired
    void injectUserRepository(UserRepository userRepository) {
        log.debug("Injecting user repository via method injection");
        this.userRepository = userRepository;
    }

    @Transactional
    public Team createTeam(Team team) {
        log.info("Creating team {}", team.getName());
        return teamRepository.save(team);
    }

    @Transactional
    public Optional<User> attachLead(UUID teamId, Long userId) {
        Optional<Team> team = teamRepository.findById(teamId);
        Optional<User> user = userRepository.findById(userId);
        if (team.isPresent() && user.isPresent()) {
            log.info("Setting user {} as lead for team {}", userId, teamId);
            user.get().setLeadTeam(team.get());
            team.get().setLead(user.get());
            return user;
        }
        log.warn("Unable to set lead: team {} or user {} missing", teamId, userId);
        return Optional.empty();
    }

    @Transactional
    public long countSupporters(UUID teamId) {
        long supporters = teamRepository.countSupporters(teamId);
        log.debug("Team {} has {} supporters", teamId, supporters);
        return supporters;
    }

    @Transactional
    public List<Team> findByMemberEmail(String email) {
        log.debug("Finding teams for member email {}", email);
        return teamRepository.findTeamsForMemberEmail(email);
    }
}
