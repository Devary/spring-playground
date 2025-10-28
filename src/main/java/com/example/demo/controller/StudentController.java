package com.example.demo.controller;

import com.example.demo.entity.Student;
import com.example.demo.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/students")
@Validated
@RequiredArgsConstructor
@Tag(name = "Students", description = "Operations related to student resources")
@SecurityRequirement(name = "apiToken")
public class StudentController {

    private final StudentService studentService;

    @GetMapping
    @Operation(
            summary = "List students",
            description = "Retrieve all students or filter the results by the title of an enrolled course.")
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "Students retrieved successfully",
                content = @Content(
                        mediaType = "application/json",
                        array = @ArraySchema(schema = @Schema(implementation = Student.class)))),
        @ApiResponse(responseCode = "401", description = "Request rejected due to a missing or invalid API token",
                content = @Content)
    })
    public ResponseEntity<List<Student>> findStudents(
            @Parameter(
                            name = "course",
                            in = ParameterIn.QUERY,
                            description = "Course title used to filter the student list",
                            example = "Introduction to Databases")
                    @RequestParam(name = "course", required = false)
                    String courseTitle,
            @Parameter(
                            name = "X-Trace-Id",
                            in = ParameterIn.HEADER,
                            description = "Optional trace identifier propagated by the client",
                            example = "trace-12345")
                    @RequestHeader(name = "X-Trace-Id", required = false)
                    String traceId) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Trace-Id", traceId != null ? traceId : "generated-" + System.currentTimeMillis());
        List<Student> students =
                courseTitle == null ? studentService.findAll() : studentService.findByCourseTitle(courseTitle);
        return new ResponseEntity<>(students, headers, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a student", description = "Fetch a student by their identifier.")
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "Student found",
                content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = Student.class))),
        @ApiResponse(responseCode = "404", description = "Student not found", content = @Content),
        @ApiResponse(responseCode = "401", description = "Request rejected due to a missing or invalid API token",
                content = @Content)
    })
    public ResponseEntity<Student> findStudentById(
            @Parameter(description = "Identifier of the student", required = true)
                    @PathVariable
                    Long id) {
        return studentService.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create a student", description = "Create a new student record.")
    @ApiResponses({
        @ApiResponse(
                responseCode = "201",
                description = "Student created successfully",
                content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = Student.class))),
        @ApiResponse(responseCode = "400", description = "Validation error", content = @Content),
        @ApiResponse(responseCode = "401", description = "Request rejected due to a missing or invalid API token",
                content = @Content)
    })
    public ResponseEntity<Student> createStudent(
            @Parameter(description = "Student payload", required = true)
                    @Valid @RequestBody
                    Student student) {
        Student created = studentService.createStudent(student);
        return ResponseEntity.created(URI.create("/api/students/" + created.getId())).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Replace a student", description = "Update an existing student with the provided payload.")
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "Student updated successfully",
                content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = Student.class))),
        @ApiResponse(responseCode = "404", description = "Student not found", content = @Content),
        @ApiResponse(responseCode = "400", description = "Validation error", content = @Content),
        @ApiResponse(responseCode = "401", description = "Request rejected due to a missing or invalid API token",
                content = @Content)
    })
    public ResponseEntity<Student> updateStudent(
            @Parameter(description = "Identifier of the student", required = true)
                    @PathVariable
                    Long id,
            @Parameter(description = "Updated student payload", required = true)
                    @Valid @RequestBody
                    Student student) {
        return studentService.updateStudent(id, student).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}")
    @Operation(
            summary = "Update student email",
            description = "Partially update a student's email address using a query parameter.")
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "Email updated successfully",
                content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = Student.class))),
        @ApiResponse(responseCode = "404", description = "Student not found", content = @Content),
        @ApiResponse(responseCode = "400", description = "Invalid email provided", content = @Content),
        @ApiResponse(responseCode = "401", description = "Request rejected due to a missing or invalid API token",
                content = @Content)
    })
    public ResponseEntity<Student> patchEmail(
            @Parameter(description = "Identifier of the student", required = true)
                    @PathVariable
                    Long id,
            @Parameter(
                            name = "email",
                            in = ParameterIn.QUERY,
                            description = "New email address for the student",
                            example = "student@example.com",
                            required = true)
                    @RequestParam("email")
                    String email) {
        return studentService.partialUpdate(id, email).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a student", description = "Remove a student permanently.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Student deleted", content = @Content),
        @ApiResponse(responseCode = "404", description = "Student not found", content = @Content),
        @ApiResponse(responseCode = "401", description = "Request rejected due to a missing or invalid API token",
                content = @Content)
    })
    public ResponseEntity<Void> deleteStudent(
            @Parameter(description = "Identifier of the student", required = true)
                    @PathVariable
                    Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{studentId}/advisor/{professorId}")
    @Operation(
            summary = "Assign an advisor",
            description = "Assign a professor as the advisor for the given student.")
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "Advisor assigned",
                content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = Student.class))),
        @ApiResponse(responseCode = "404", description = "Student or professor not found", content = @Content),
        @ApiResponse(responseCode = "401", description = "Request rejected due to a missing or invalid API token",
                content = @Content)
    })
    public ResponseEntity<Student> assignAdvisor(
            @Parameter(description = "Identifier of the student", required = true)
                    @PathVariable
                    Long studentId,
            @Parameter(description = "Identifier of the professor", required = true)
                    @PathVariable
                    Long professorId) {
        return studentService.assignAdvisor(studentId, professorId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.HEAD)
    @Operation(summary = "Check if a student exists", description = "HEAD endpoint used to check if a student is present.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Student exists", content = @Content),
        @ApiResponse(responseCode = "404", description = "Student not found", content = @Content),
        @ApiResponse(responseCode = "401", description = "Request rejected due to a missing or invalid API token",
                content = @Content)
    })
    public ResponseEntity<Void> headStudent(
            @Parameter(description = "Identifier of the student", required = true)
                    @PathVariable
                    Long id) {
        boolean exists = studentService.findById(id).isPresent();
        return exists ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    @RequestMapping(method = RequestMethod.OPTIONS)
    @Operation(summary = "Discover supported student operations",
            description = "OPTIONS endpoint to list the HTTP methods allowed for the student resource.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Allowed methods retrieved", content = @Content)
    })
    public ResponseEntity<Void> options() {
        return ResponseEntity.ok()
                .allow(HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT, HttpMethod.PATCH, HttpMethod.DELETE,
                        HttpMethod.HEAD, HttpMethod.OPTIONS)
                .build();
    }
}
