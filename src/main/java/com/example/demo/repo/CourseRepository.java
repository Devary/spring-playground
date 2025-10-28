package com.example.demo.repo;

import com.example.demo.entity.Course;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CourseRepository extends JpaRepository<Course, UUID> {

    @Query("select count(s) from Student s where s.course.id = :courseId")
    long countStudents(@Param("courseId") UUID courseId);

    @Query("select c from Course c join c.professor p where lower(p.email) = lower(:email)")
    List<Course> findByProfessorEmail(@Param("email") String email);
}
