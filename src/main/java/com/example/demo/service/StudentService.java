package com.example.demo.service;

import com.example.demo.entity.Course;
import com.example.demo.entity.Professor;
import com.example.demo.entity.Student;
import com.example.demo.repo.CourseRepository;
import com.example.demo.repo.ProfessorRepository;
import com.example.demo.repo.StudentRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final ProfessorRepository professorRepository;

    @Transactional
    public Student createStudent(Student student) {
        log.info("Creating student {}", student.getEmail());
        student.setCourse(resolveCourse(student.getCourse()));
        student.setAdvisor(resolveProfessor(student.getAdvisor()));
        return studentRepository.save(student);
    }

    @Transactional
    public Optional<Student> updateStudent(Long id, Student student) {
        return studentRepository.findById(id).map(existing -> {
            log.info("Updating student {}", id);
            existing.setName(student.getName());
            existing.setEmail(student.getEmail());
            existing.setCourse(resolveCourse(student.getCourse()));
            existing.setAdvisor(resolveProfessor(student.getAdvisor()));
            return existing;
        });
    }

    @Transactional
    public Optional<Student> partialUpdate(Long id, String email) {
        return studentRepository.findById(id).map(existing -> {
            log.info("Updating student {} email to {}", id, email);
            existing.setEmail(email);
            return existing;
        });
    }

    @Transactional
    public void deleteStudent(Long id) {
        log.warn("Deleting student {}", id);
        studentRepository.deleteById(id);
    }

    @Transactional
    public Optional<Student> assignAdvisor(Long studentId, Long professorId) {
        return studentRepository.findById(studentId)
                .flatMap(student -> professorRepository.findById(professorId).map(professor -> {
                    log.info("Assigning professor {} as advisor for student {}", professorId, studentId);
                    student.setAdvisor(professor);
                    return student;
                }));
    }

    @Transactional
    public List<Student> findByCourseTitle(String courseTitle) {
        log.debug("Finding students by course title like {}", courseTitle);
        return studentRepository.findByCourseTitleContainingIgnoreCase(courseTitle);
    }

    @Transactional
    public Optional<Student> findById(Long id) {
        log.debug("Finding student by id {}", id);
        return studentRepository.findById(id);
    }

    @Transactional
    public List<Student> findAll() {
        log.debug("Retrieving all students");
        return studentRepository.findAll();
    }

    private Course resolveCourse(Course candidate) {
        if (candidate == null || candidate.getId() == null) {
            return candidate;
        }
        return courseRepository.findById(candidate.getId()).orElse(candidate);
    }

    private Professor resolveProfessor(Professor candidate) {
        if (candidate == null || candidate.getId() == null) {
            return candidate;
        }
        return professorRepository.findById(candidate.getId()).orElse(candidate);
    }
}
