package com.anas.StudentRestApis.Repository;

import com.anas.StudentRestApis.Entity.CollegeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CollegeRepository extends JpaRepository<CollegeEntity, Long> {
    CollegeEntity findByCollegeCode(String code);
}
