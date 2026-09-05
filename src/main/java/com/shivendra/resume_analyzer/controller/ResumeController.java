package com.shivendra.resume_analyzer.controller;


import com.shivendra.resume_analyzer.model.Resume;
import com.shivendra.resume_analyzer.repository.ResumeRepository;
import com.shivendra.resume_analyzer.service.PdfTextExtractorService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.shivendra.resume_analyzer.service.AiClientService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/resumes")
public class ResumeController {

    private final ResumeRepository resumeRepository;
    private final PdfTextExtractorService pdfTextExtractorService;
    private final AiClientService aiClientService;

    public ResumeController(ResumeRepository resumeRepository,
                            PdfTextExtractorService pdfTextExtractorService,
                            AiClientService aiClientService) {
        this.resumeRepository = resumeRepository;
        this.pdfTextExtractorService = pdfTextExtractorService;
        this.aiClientService = aiClientService;
    }

    @PostMapping("/upload")
    public Resume uploadResume(@RequestParam("file") MultipartFile file) throws IOException {
        String extractedText = pdfTextExtractorService.extractText(file);

        Resume resume = new Resume();
        resume.setFileName(file.getOriginalFilename());
        resume.setExtractedText(extractedText);

        return resumeRepository.save(resume);
    }

    @GetMapping
    public List<Resume> getAllResumes() {
        return resumeRepository.findAll();
    }

    @GetMapping("/{id}")
    public Resume getResumeById(@PathVariable Long id) {
        return resumeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resume not found with id " + id));
    }
    @GetMapping("/{id}/skills")
    public String getResumeSkills(@PathVariable Long id) {
        Resume resume = resumeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resume not found with id " + id));

        return aiClientService.extractSkillsFromResume(resume.getExtractedText());
    }
}