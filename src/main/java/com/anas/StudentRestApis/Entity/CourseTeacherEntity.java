package com.anas.StudentRestApis.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * CourseTeacherEntity - Represents a many-to-many relationship between courses
 * and teachers.
 *
 * This entity maintains the assignment of teachers to courses with metadata
 * about
 * when the assignment was made and by whom. It enables tracking of which
 * teachers
 * teach which courses in the system.
 *
 * Database Table: course_teachers
 * - Has unique constraint on (course_id, teacher_id) to prevent duplicate
 * assignments
 * - Indexed on course_id and teacher_id for fast queries
 *
 * Cascade Behavior:
 * - When a course is deleted, all related assignments are deleted
 * - When a teacher is deleted, all related assignments are deleted
 */
@Entity
@Table(name = "course_teachers", indexes = {
                @Index(name = "idx_course_id", columnList = "course_id"),
                @Index(name = "idx_teacher_id", columnList = "teacher_id"),
                @Index(name = "idx_course_teacher_unique", columnList = "course_id, teacher_id", unique = true)
})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CourseTeacherEntity {

        /** Primary key - auto-generated unique assignment ID */
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long assignmentId;

        /** Many-to-One relationship to CourseEntity. Lazy loading for performance. */
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "course_id", nullable = false)
        private CourseEntity course;

        /** Many-to-One relationship to TeacherEntity. Lazy loading for performance. */
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "teacher_id", nullable = false)
        private TeacherEntity teacher;

        /**
         * Timestamp when assignment was created. Automatically set on insert, cannot be
         * updated.
         */
        @Column(name = "assigned_at", nullable = false, updatable = false)
        @CreationTimestamp
        private LocalDateTime assignedAt;

        /**
         * Username/ID of the admin user who made this assignment. Used for audit
         * tracking.
         */
        @Column(name = "assigned_by")
        private String assignedBy;

        /**
         * Optional notes about the assignment (e.g., primary instructor, role-specific
         * info).
         */
        @Column(name = "notes")
        private String notes;
}
