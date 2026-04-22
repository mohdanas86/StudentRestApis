package com.anas.StudentRestApis.Repository;

import com.anas.StudentRestApis.Entity.CourseTeacherEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * CourseTeacherRepository - Data access layer for CourseTeacherEntity.
 *
 * Provides CRUD operations and custom queries for managing course-teacher
 * assignments.
 * Uses Spring Data JPA with custom @Query annotations to handle nested property
 * references.
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
    @Query("SELECT ct FROM CourseTeacherEntity ct WHERE ct.course.courseId = :courseId")
    List<CourseTeacherEntity> findByCourseId(@Param("courseId") Long courseId);

    /**
     * Find all courses assigned to a specific teacher.
     *
     * @param teacherId the ID of the teacher
     * @return List of CourseTeacherEntity records for the teacher. Empty list if no
     *         assignments.
     */
    @Query("SELECT ct FROM CourseTeacherEntity ct WHERE ct.teacher.teacherId = :teacherId")
    List<CourseTeacherEntity> findByTeacherId(@Param("teacherId") Long teacherId);

    /**
     * Find a specific course-teacher assignment.
     *
     * @param courseId  the ID of the course
     * @param teacherId the ID of the teacher
     * @return Optional containing the assignment if it exists, empty otherwise
     */
    @Query("SELECT ct FROM CourseTeacherEntity ct WHERE ct.course.courseId = :courseId AND ct.teacher.teacherId = :teacherId")
    Optional<CourseTeacherEntity> findByCourseIdAndTeacherId(@Param("courseId") Long courseId, @Param("teacherId") Long teacherId);

    /**
     * Check if a teacher is assigned to a course (existence check).
     *
     * @param courseId  the ID of the course
     * @param teacherId the ID of the teacher
     * @return true if assignment exists, false otherwise
     */
    @Query("SELECT COUNT(ct) > 0 FROM CourseTeacherEntity ct WHERE ct.course.courseId = :courseId AND ct.teacher.teacherId = :teacherId")
    boolean existsByCourseIdAndTeacherId(@Param("courseId") Long courseId, @Param("teacherId") Long teacherId);

    /**
     * Count the number of teachers assigned to a course.
     *
     * @param courseId the ID of the course
     * @return the count of assigned teachers
     */
    @Query("SELECT COUNT(ct) FROM CourseTeacherEntity ct WHERE ct.course.courseId = :courseId")
    long countByCourseId(@Param("courseId") Long courseId);

    /**
     * Count the number of courses assigned to a teacher.
     *
     * @param teacherId the ID of the teacher
     * @return the count of assigned courses
     */
    @Query("SELECT COUNT(ct) FROM CourseTeacherEntity ct WHERE ct.teacher.teacherId = :teacherId")
    long countByTeacherId(@Param("teacherId") Long teacherId);

    /**
     * Delete a specific course-teacher assignment.
     *
     * @param courseId  the ID of the course
     * @param teacherId the ID of the teacher
     */
    @Modifying
    @Query("DELETE FROM CourseTeacherEntity ct WHERE ct.course.courseId = :courseId AND ct.teacher.teacherId = :teacherId")
    void deleteByCourseIdAndTeacherId(@Param("courseId") Long courseId, @Param("teacherId") Long teacherId);

    /**
     * Find all teachers for a course with pagination support.
     * Useful for large courses with many teachers.
     *
     * @param courseId the ID of the course
     * @param pageable pagination information (page number, size, sorting)
     * @return Page of CourseTeacherEntity records
     */
    @Query("SELECT ct FROM CourseTeacherEntity ct WHERE ct.course.courseId = :courseId")
    Page<CourseTeacherEntity> findByCourseId(@Param("courseId") Long courseId, Pageable pageable);
}
