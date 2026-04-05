package com.anas.StudentRestApis.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

/**
 * TeacherEntity - Represents a teacher in the system
 * Each teacher is uniquely identified by employeeId and email.
 * A teacher can teach multiple courses across different colleges.
 */
@Entity
@Table(name = "teachers", indexes = {
        @Index(name = "idx_employee_id", columnList = "employee_id"),
        @Index(name = "idx_email", columnList = "email"),
        @Index(name = "idx_is_active", columnList = "is_active")
})
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TeacherEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long teacherId;

    @Column(unique = true, nullable = false)
    private String employeeId;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(unique = true, nullable = false)
    private String email;

    @Column
    private String specialization;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT true")
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @ManyToMany(mappedBy = "teachers")
    @JsonIgnoreProperties("teachers")
    private List<CourseEntity> courses;
}