package com.anas.StudentRestApis.Repository;

import com.anas.StudentRestApis.Entity.CourseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository extends JpaRepository<CourseEntity, Long> {
    CourseEntity findByCourseCode(String code);
}
