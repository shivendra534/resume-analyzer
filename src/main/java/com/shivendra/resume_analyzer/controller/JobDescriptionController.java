package com.shivendra.resume_analyzer.controller;

import com.shivendra.resume_analyzer.model.JobDescription;
import com.shivendra.resume_analyzer.repository.JobDescriptionRepository;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
public class JobDescriptionController {

    private final JobDescriptionRepository jobDescriptionRepository;

    public JobDescriptionController(JobDescriptionRepository jobDescriptionRepository) {
        this.jobDescriptionRepository = jobDescriptionRepository;
    }

    @PostMapping
    public JobDescription createJob(@Valid @RequestBody JobDescription job) {
        return jobDescriptionRepository.save(job);
    }

    @GetMapping
    public List<JobDescription> getAllJobs() {
        return jobDescriptionRepository.findAll();
    }

    @GetMapping("/{id}")
    public JobDescription getJobById(@PathVariable Long id) {
        return jobDescriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found with id " + id));
    }
}