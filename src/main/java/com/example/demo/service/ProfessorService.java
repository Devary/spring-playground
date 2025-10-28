package com.example.demo.service;

import com.example.demo.entity.Professor;
import com.example.demo.repo.ProfessorRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfessorService {

    private final ProfessorRepository professorRepository;

    @Transactional
    public Professor createProfessor(Professor professor) {
        log.info("Creating professor {}", professor.getEmail());
        return professorRepository.save(professor);
    }

    @Transactional
    public Optional<Professor> findById(Long id) {
        log.debug("Finding professor by id {}", id);
        return professorRepository.findById(id);
    }

    @Transactional
    public List<Professor> findAll() {
        log.debug("Retrieving all professors");
        return professorRepository.findAll();
    }

    @Transactional
    public void deleteProfessor(Long id) {
        log.warn("Deleting professor {}", id);
        professorRepository.deleteById(id);
    }
}
