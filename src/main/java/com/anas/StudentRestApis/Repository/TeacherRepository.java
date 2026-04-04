package com.anas.StudentRestApis.Repository;

import com.anas.StudentRestApis.Entity.TeacherEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeacherRepository extends JpaRepository<TeacherEntity, Long> {
    TeacherEntity findByEmail(String email);
}
