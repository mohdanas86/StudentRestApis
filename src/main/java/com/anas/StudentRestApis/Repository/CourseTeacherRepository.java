package com.anas.StudentRestApis.Repository;

import com.anas.StudentRestApis.Entity.CourseTeacherEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * CourseTeacherRepository - Data access layer for CourseTeacherEntity.
 *
 * Provides CRUD operations and custom queries for managing course-teacher
 * assignments.
 * Uses Spring Data JPA for automatic query generation based on method naming
 * conventions.
 *
 * Methods:
 * - findByCourseId: Retrieve all teachers assigned to a specific course
 * - findByTeacherId: Retrieve all courses assigned to a specific teacher
 * - findByCourseIdAndTeacherId: Retrieve a specific assignment (optional)
 * - existsByCourseIdAndTeacherId: Check if an assignment exists
 * - countByCourseId: Get count of teachers for a course
 * - countByTeacherId: Get count of courses for a teacher
 * - deleteByCourseIdAndTeacherId: Remove a specific assignment
 */
@Repository
public interface CourseTeacherRepository extends JpaRepository<CourseTeacherEntity, Long> {

    /**
     * Find all teachers assigned to a specific course.
     *
     * @param courseId the ID of the course
     * @return List of CourseTeacherEntity records for the course. Empty list if no
     *         assignments.
     */
    List<CourseTeacherEntity> findByCourseId(Long courseId);

    /**
     * Find all courses assigned to a specific teacher.
     *
     * @param teacherId the ID of the teacher
     * @return List of CourseTeacherEntity records for the teacher. Empty list if no
     *         assignments.
     */
    List<CourseTeacherEntity> findByTeacherId(Long teacherId);

    /**
     * Find a specific course-teacher assignment.
     *
     * @param courseId  the ID of the course
     * @param teacherId the ID of the teacher
     * @return Optional containing the assignment if it exists, empty otherwise
     */
    Optional<CourseTeacherEntity> findByCourseIdAndTeacherId(Long courseId, Long teacherId);

    /**
     * Check if a teacher is assigned to a course (existence check).
     *
     * @param courseId  the ID of the course
     * @param teacherId the ID of the teacher
     * @return true if assignment exists, false otherwise
     */
    boolean existsByCourseIdAndTeacherId(Long courseId, Long teacherId);

    /**
     * Count the number of teachers assigned to a course.
     *
     * @param courseId the ID of the course
     * @return the count of assigned teachers
     */
    long countByCourseId(Long courseId);

    /**
     * Count the number of courses assigned to a teacher.
     *
     * @param teacherId the ID of the teacher
     * @return the count of assigned courses
     */
    long countByTeacherId(Long teacherId);

    /**
     * Delete a specific course-teacher assignment.
     *
     * @param courseId  the ID of the course
     * @param teacherId the ID of the teacher
     */
    void deleteByCourseIdAndTeacherId(Long courseId, Long teacherId);

    /**
     * Find all teachers for a course with pagination support.
     * Useful for large courses with many teachers.
     *
     * @param courseId the ID of the course
     * @param pageable pagination information (page number, size, sorting)
     * @return Page of CourseTeacherEntity records
     */
    Page<CourseTeacherEntity> findByCourseId(Long courseId, Pageable pageable);
}
