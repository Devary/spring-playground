package com.example.demo.service;

import com.example.demo.entity.Course;
import com.example.demo.entity.Professor;
import com.example.demo.repo.CourseRepository;
import com.example.demo.repo.ProfessorRepository;
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
public class CourseService {

    private final CourseRepository courseRepository;
    private final ProfessorRepository professorRepository;

    @Transactional
    public Course createCourse(Course course) {
        log.info("Creating course {}", course.getTitle());
        course.setProfessor(resolveProfessor(course.getProfessor()));
        return courseRepository.save(course);
    }

    @Transactional
    public Optional<Course> assignProfessor(UUID courseId, Long professorId) {
        Optional<Course> course = courseRepository.findById(courseId);
        Optional<Professor> professor = professorRepository.findById(professorId);
        if (course.isPresent() && professor.isPresent()) {
            log.info("Assigning professor {} to course {}", professorId, courseId);
            course.get().setProfessor(professor.get());
            return course;
        }
        log.warn("Unable to assign professor: course {} or professor {} missing", courseId, professorId);
        return Optional.empty();
    }

    @Transactional
    public long countStudents(UUID courseId) {
        long students = courseRepository.countStudents(courseId);
        log.debug("Course {} has {} enrolled students", courseId, students);
        return students;
    }

    @Transactional
    public List<Course> findByProfessorEmail(String email) {
        log.debug("Finding courses for professor email {}", email);
        return courseRepository.findByProfessorEmail(email);
    }

    private Professor resolveProfessor(Professor candidate) {
        if (candidate == null || candidate.getId() == null) {
            return candidate;
        }
        return professorRepository.findById(candidate.getId()).orElse(candidate);
    }
}
