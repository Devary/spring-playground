package com.example.demo.controller;

import com.example.demo.entity.Course;
import com.example.demo.service.CourseService;
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
public class CourseController {

    private final CourseService courseService;

    @PostMapping
    public ResponseEntity<Course> createCourse(@Valid @RequestBody Course course) {
        Course created = courseService.createCourse(course);
        return ResponseEntity.created(URI.create("/api/courses/" + created.getId())).body(created);
    }

    @PostMapping("/{courseId}/professor/{professorId}")
    public ResponseEntity<Course> assignProfessor(
            @PathVariable UUID courseId, @PathVariable Long professorId) {
        return courseService.assignProfessor(courseId, professorId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{courseId}/students/count")
    public ResponseEntity<Long> countStudents(@PathVariable UUID courseId) {
        return ResponseEntity.ok(courseService.countStudents(courseId));
    }

    @GetMapping
    public ResponseEntity<List<Course>> findCoursesByProfessor(
            @RequestParam(name = "professorEmail") String professorEmail) {
        List<Course> courses = courseService.findByProfessorEmail(professorEmail);
        return new ResponseEntity<>(courses, HttpStatus.OK);
    }
}
