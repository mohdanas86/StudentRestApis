package com.anas.StudentRestApis.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
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

    @OneToMany(mappedBy = "college", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("college") // FIX: Stop Course from looking back at College
    private List<CourseEntity> courses;
}