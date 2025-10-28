package com.example.demo.controller;

import com.example.demo.entity.Professor;
import com.example.demo.service.ProfessorService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/professors")
@Validated
@RequiredArgsConstructor
public class ProfessorController {

    private final ProfessorService professorService;

    @GetMapping
    public ResponseEntity<List<Professor>> findProfessors() {
        return ResponseEntity.ok(professorService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Professor> findProfessorById(@PathVariable Long id) {
        return professorService.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Professor> createProfessor(@Valid @RequestBody Professor professor) {
        Professor created = professorService.createProfessor(professor);
        return ResponseEntity.created(URI.create("/api/professors/" + created.getId())).body(created);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProfessor(@PathVariable Long id) {
        professorService.deleteProfessor(id);
        return ResponseEntity.noContent().build();
    }
}
