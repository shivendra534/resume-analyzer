package com.shivendra.resume_analyzer.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "match_results")
@Data
public class MatchResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long resumeId;

    private Long jobId;

    private Double matchScore;

    @Column(columnDefinition = "TEXT")
    private String suggestions;

    private LocalDateTime createdAt = LocalDateTime.now();
}