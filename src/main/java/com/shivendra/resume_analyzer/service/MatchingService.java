package com.shivendra.resume_analyzer.service;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MatchingService {

    public double calculateMatchScore(String resumeText, String jobText) {
        Set<String> resumeWords = tokenize(resumeText);
        Set<String> jobWords = tokenize(jobText);

        if (jobWords.isEmpty()) {
            return 0.0;
        }

        Set<String> commonWords = new HashSet<>(resumeWords);
        commonWords.retainAll(jobWords);

        double score = ((double) commonWords.size() / jobWords.size()) * 100;
        return Math.round(score * 100.0) / 100.0; // round to 2 decimal places
    }

    private Set<String> tokenize(String text) {
        if (text == null) return new HashSet<>();

        return Arrays.stream(text.toLowerCase().split("\\W+"))
                .filter(word -> word.length() > 2) // ignore tiny/common words
                .collect(Collectors.toSet());
    }
}