package com.example.demo.controller;

import com.example.demo.entity.Professor;
import com.example.demo.service.ProfessorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Professors", description = "Operations related to professor resources")
@SecurityRequirement(name = "apiToken")
public class ProfessorController {

    private final ProfessorService professorService;

    @GetMapping
    @Operation(summary = "List professors", description = "Retrieve all professors.")
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "Professors retrieved successfully",
                content = @Content(
                        mediaType = "application/json",
                        array = @ArraySchema(schema = @Schema(implementation = Professor.class)))),
        @ApiResponse(responseCode = "401", description = "Request rejected due to a missing or invalid API token",
                content = @Content)
    })
    public ResponseEntity<List<Professor>> findProfessors() {
        return ResponseEntity.ok(professorService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a professor", description = "Fetch a professor by their identifier.")
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "Professor found",
                content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = Professor.class))),
        @ApiResponse(responseCode = "404", description = "Professor not found", content = @Content),
        @ApiResponse(responseCode = "401", description = "Request rejected due to a missing or invalid API token",
                content = @Content)
    })
    public ResponseEntity<Professor> findProfessorById(
            @Parameter(description = "Identifier of the professor", required = true)
                    @PathVariable
                    Long id) {
        return professorService.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create a professor", description = "Create a new professor record.")
    @ApiResponses({
        @ApiResponse(
                responseCode = "201",
                description = "Professor created successfully",
                content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = Professor.class))),
        @ApiResponse(responseCode = "400", description = "Validation error", content = @Content),
        @ApiResponse(responseCode = "401", description = "Request rejected due to a missing or invalid API token",
                content = @Content)
    })
    public ResponseEntity<Professor> createProfessor(
            @Parameter(description = "Professor payload", required = true)
                    @Valid @RequestBody
                    Professor professor) {
        Professor created = professorService.createProfessor(professor);
        return ResponseEntity.created(URI.create("/api/professors/" + created.getId())).body(created);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a professor", description = "Remove a professor permanently.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Professor deleted", content = @Content),
        @ApiResponse(responseCode = "404", description = "Professor not found", content = @Content),
        @ApiResponse(responseCode = "401", description = "Request rejected due to a missing or invalid API token",
                content = @Content)
    })
    public ResponseEntity<Void> deleteProfessor(
            @Parameter(description = "Identifier of the professor", required = true)
                    @PathVariable
                    Long id) {
        professorService.deleteProfessor(id);
        return ResponseEntity.noContent().build();
    }
}
