package com.anas.StudentRestApis.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Entity
@Table(name = "courses")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CourseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long courseId;

    @Column(unique = true, nullable = false)
    private String courseCode;

    @Column(nullable = false)
    private String courseName;

    @Column
    private String credits;

    @ManyToOne
    @JoinColumn(name="college_id", nullable = false)
    @JsonIgnoreProperties("courses") // FIX: Stop College from looking back at Courses
    private CollegeEntity college;

    @ManyToMany
    @JoinTable(
            name = "course_teachers",
            joinColumns = @JoinColumn(name = "course_id"),
            inverseJoinColumns = @JoinColumn(name = "teacher_id")
    )
    @JsonIgnoreProperties("courses")
    private List<TeacherEntity> teachers;
}