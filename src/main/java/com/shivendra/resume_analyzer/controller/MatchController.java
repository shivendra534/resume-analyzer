package com.shivendra.resume_analyzer.controller;

import com.shivendra.resume_analyzer.model.JobDescription;
import com.shivendra.resume_analyzer.model.MatchResult;
import com.shivendra.resume_analyzer.model.Resume;
import com.shivendra.resume_analyzer.repository.JobDescriptionRepository;
import com.shivendra.resume_analyzer.repository.MatchResultRepository;
import com.shivendra.resume_analyzer.repository.ResumeRepository;
import com.shivendra.resume_analyzer.service.MatchingService;
import com.shivendra.resume_analyzer.service.EmbeddingService;
import org.springframework.web.bind.annotation.*;
import com.shivendra.resume_analyzer.service.AiClientService;

import java.util.List;

@RestController
@RequestMapping("/api/match")
public class MatchController {

    private final ResumeRepository resumeRepository;
    private final JobDescriptionRepository jobDescriptionRepository;
    private final MatchResultRepository matchResultRepository;
    private final MatchingService matchingService;
    private final EmbeddingService embeddingService;
    private final AiClientService aiClientService;


    public MatchController(ResumeRepository resumeRepository,
                           JobDescriptionRepository jobDescriptionRepository,
                           MatchResultRepository matchResultRepository,
                           MatchingService matchingService,
                           EmbeddingService embeddingService,
                           AiClientService aiClientService) {
        this.resumeRepository = resumeRepository;
        this.jobDescriptionRepository = jobDescriptionRepository;
        this.matchResultRepository = matchResultRepository;
        this.matchingService = matchingService;
        this.embeddingService = embeddingService;
        this.aiClientService = aiClientService;
    }

    @PostMapping
    public MatchResult matchResumeToJob(@RequestParam Long resumeId, @RequestParam Long jobId) {

        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new RuntimeException("Resume not found with id " + resumeId));

        JobDescription job = jobDescriptionRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found with id " + jobId));

        double score = matchingService.calculateMatchScore(
                resume.getExtractedText(),
                job.getDescription()
        );

        MatchResult result = new MatchResult();
        result.setResumeId(resumeId);
        result.setJobId(jobId);
        result.setMatchScore(score);

        return matchResultRepository.save(result);
    }

    @PostMapping("/semantic")
    public MatchResult matchSemantic(@RequestParam Long resumeId, @RequestParam Long jobId) {

        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new RuntimeException("Resume not found with id " + resumeId));

        JobDescription job = jobDescriptionRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found with id " + jobId));

        List<Double> resumeEmbedding = embeddingService.getEmbedding(resume.getExtractedText());
        List<Double> jobEmbedding = embeddingService.getEmbedding(job.getDescription());

        double similarity = embeddingService.cosineSimilarity(resumeEmbedding, jobEmbedding);
        double scorePercent = Math.round(similarity * 10000.0) / 100.0;

        MatchResult result = new MatchResult();
        result.setResumeId(resumeId);
        result.setJobId(jobId);
        result.setMatchScore(scorePercent);

        return matchResultRepository.save(result);
    }
    @PostMapping("/suggestions")
    public MatchResult generateMatchSuggestions(@RequestParam Long resumeId, @RequestParam Long jobId) {

        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new RuntimeException("Resume not found with id " + resumeId));

        JobDescription job = jobDescriptionRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found with id " + jobId));

        String suggestions = aiClientService.generateSuggestions(
                resume.getExtractedText(),
                job.getDescription()
        );

        MatchResult result = new MatchResult();
        result.setResumeId(resumeId);
        result.setJobId(jobId);
        result.setSuggestions(suggestions);

        return matchResultRepository.save(result);
    }
}