package com.shivendra.resume_analyzer.repository;

import com.shivendra.resume_analyzer.model.JobDescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobDescriptionRepository extends JpaRepository<JobDescription, Long> {
}