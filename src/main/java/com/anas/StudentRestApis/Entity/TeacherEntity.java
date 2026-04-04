package com.anas.StudentRestApis.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Entity
@Table(name = "teachers")
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

    @ManyToMany(mappedBy = "teachers")
    @JsonIgnoreProperties("teachers")
    private List<CourseEntity> courses;
}