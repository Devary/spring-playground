package com.example.demo.controller;

import com.example.demo.entity.Course;
import com.example.demo.service.CourseService;
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
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/courses")
@Validated
@RequiredArgsConstructor
@Tag(name = "Courses", description = "Operations related to course resources")
@SecurityRequirement(name = "apiToken")
public class CourseController {

    private final CourseService courseService;

    @PostMapping
    @Operation(summary = "Create a course", description = "Create a new course definition.")
    @ApiResponses({
        @ApiResponse(
                responseCode = "201",
                description = "Course created successfully",
                content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = Course.class))),
        @ApiResponse(responseCode = "400", description = "Validation error", content = @Content),
        @ApiResponse(responseCode = "401", description = "Request rejected due to a missing or invalid API token",
                content = @Content)
    })
    public ResponseEntity<Course> createCourse(
            @Parameter(description = "Course payload", required = true)
                    @Valid @RequestBody
                    Course course) {
        Course created = courseService.createCourse(course);
        return ResponseEntity.created(URI.create("/api/courses/" + created.getId())).body(created);
    }

    @PostMapping("/{courseId}/professor/{professorId}")
    @Operation(
            summary = "Assign a professor to a course",
            description = "Associate a professor with a course by their identifiers.")
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "Professor assigned",
                content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = Course.class))),
        @ApiResponse(responseCode = "404", description = "Course or professor not found", content = @Content),
        @ApiResponse(responseCode = "401", description = "Request rejected due to a missing or invalid API token",
                content = @Content)
    })
    public ResponseEntity<Course> assignProfessor(
            @Parameter(description = "Identifier of the course", required = true)
                    @PathVariable
                    UUID courseId,
            @Parameter(description = "Identifier of the professor", required = true)
                    @PathVariable
                    Long professorId) {
        return courseService.assignProfessor(courseId, professorId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{courseId}/students/count")
    @Operation(summary = "Count enrolled students", description = "Count the students assigned to the given course.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Count returned",
                content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = Long.class))),
        @ApiResponse(responseCode = "404", description = "Course not found", content = @Content),
        @ApiResponse(responseCode = "401", description = "Request rejected due to a missing or invalid API token",
                content = @Content)
    })
    public ResponseEntity<Long> countStudents(
            @Parameter(description = "Identifier of the course", required = true)
                    @PathVariable
                    UUID courseId) {
        return ResponseEntity.ok(courseService.countStudents(courseId));
    }

    @GetMapping
    @Operation(
            summary = "Find courses by professor",
            description = "Retrieve courses taught by the professor with the provided email address.")
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "Courses retrieved successfully",
                content = @Content(
                        mediaType = "application/json",
                        array = @ArraySchema(schema = @Schema(implementation = Course.class)))),
        @ApiResponse(responseCode = "401", description = "Request rejected due to a missing or invalid API token",
                content = @Content)
    })
    public ResponseEntity<List<Course>> findCoursesByProfessor(
            @Parameter(
                            name = "professorEmail",
                            in = ParameterIn.QUERY,
                            description = "Email address of the professor used to filter courses",
                            example = "professor@example.com",
                            required = true)
                    @RequestParam(name = "professorEmail")
                    String professorEmail) {
        List<Course> courses = courseService.findByProfessorEmail(professorEmail);
        return new ResponseEntity<>(courses, HttpStatus.OK);
    }
}
