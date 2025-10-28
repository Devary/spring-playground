package com.example.demo.repo;

import com.example.demo.entity.Student;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {

    List<Student> findByCourseTitleContainingIgnoreCase(String courseTitle);
}
