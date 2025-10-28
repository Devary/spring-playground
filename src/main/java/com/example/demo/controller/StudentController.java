package com.example.demo.controller;

import com.example.demo.entity.Student;
import com.example.demo.service.StudentService;
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
public class StudentController {

    private final StudentService studentService;

    @GetMapping
    public ResponseEntity<List<Student>> findStudents(
            @RequestParam(name = "course", required = false) String courseTitle,
            @RequestHeader(name = "X-Trace-Id", required = false) String traceId) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Trace-Id", traceId != null ? traceId : "generated-" + System.currentTimeMillis());
        List<Student> students =
                courseTitle == null ? studentService.findAll() : studentService.findByCourseTitle(courseTitle);
        return new ResponseEntity<>(students, headers, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Student> findStudentById(@PathVariable Long id) {
        return studentService.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Student> createStudent(@Valid @RequestBody Student student) {
        Student created = studentService.createStudent(student);
        return ResponseEntity.created(URI.create("/api/students/" + created.getId())).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Student> updateStudent(@PathVariable Long id, @Valid @RequestBody Student student) {
        return studentService.updateStudent(id, student).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Student> patchEmail(@PathVariable Long id, @RequestParam("email") String email) {
        return studentService.partialUpdate(id, email).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{studentId}/advisor/{professorId}")
    public ResponseEntity<Student> assignAdvisor(@PathVariable Long studentId, @PathVariable Long professorId) {
        return studentService.assignAdvisor(studentId, professorId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.HEAD)
    public ResponseEntity<Void> headStudent(@PathVariable Long id) {
        boolean exists = studentService.findById(id).isPresent();
        return exists ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    @RequestMapping(method = RequestMethod.OPTIONS)
    public ResponseEntity<Void> options() {
        return ResponseEntity.ok()
                .allow(HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT, HttpMethod.PATCH, HttpMethod.DELETE,
                        HttpMethod.HEAD, HttpMethod.OPTIONS)
                .build();
    }
}
