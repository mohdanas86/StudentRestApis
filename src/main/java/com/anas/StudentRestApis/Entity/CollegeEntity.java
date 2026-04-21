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
 * CollegeEntity - Represents a college/institution
 */
@Entity
@Table(name = "colleges")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CollegeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long collegeId;

    @Column(unique = true, nullable = false)
    private String collegeCode;

    @Column(nullable = false)
    private String collegeName;

    @Column
    private String deanName;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "college", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("college")
    private List<CourseEntity> courses;

    /**
     * Teachers in this college - cascade PERSIST only (not DELETE)
     * When college is deleted, teachers are preserved with NULL college_id
     * This allows teachers to be reassigned to other colleges later
     */
    @OneToMany(mappedBy = "college", cascade = CascadeType.PERSIST)
    @JsonIgnoreProperties("college")
    private List<TeacherEntity> teachers;
}