package com.shivendra.resume_analyzer.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class AiClientService {

    private final RestTemplate restTemplate;

    @Value("${groq.api.key}")
    private String apiKey;

    @Value("${groq.api.url}")
    private String apiUrl;

    public AiClientService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String askAi(String prompt) {

        Map<String, Object> requestBody = Map.of(
                "model", "openai/gpt-oss-20b",
                "messages", List.of(
                        Map.of("role", "user", "content", prompt)
                )
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        Map response;
        try {
            response = restTemplate.postForObject(apiUrl, request, Map.class);
        } catch (Exception e) {
            System.out.println("GROQ CALL FAILED: " + e.getMessage());
            throw new RuntimeException("Groq API call failed: " + e.getMessage());
        }

        System.out.println("GROQ RAW RESPONSE: " + response);

        List choices = (List) response.get("choices");
        Map firstChoice = (Map) choices.get(0);
        Map message = (Map) firstChoice.get("message");

        return (String) message.get("content");
    }
    public String extractSkillsFromResume(String resumeText) {

        String prompt = """
            You are a resume parser. Extract structured information from the resume text below.

            Return ONLY valid JSON in exactly this format, with no extra text, no markdown, no explanation:
            {
              "skills": ["skill1", "skill2"],
              "experience": ["short summary of role 1", "short summary of role 2"],
              "education": ["degree, institution, year"]
            }

            If a section is not found in the resume, return an empty array for it.

            Resume text:
            %s
            """.formatted(resumeText);

        return askAi(prompt);
    }
    public String generateSuggestions(String resumeText, String jobText) {

        String prompt = """
            You are a professional resume coach. Compare the resume below against the job description.

            Provide 3 to 5 specific, actionable suggestions to improve the resume so it better matches this job.
            Focus on: missing keywords/skills, how to rephrase existing bullet points, and any gaps to address.

            Return ONLY a JSON array of strings, like this, with no extra text or markdown:
            ["suggestion 1", "suggestion 2", "suggestion 3"]

            Resume:
            %s

            Job Description:
            %s
            """.formatted(resumeText, jobText);

        return askAi(prompt);
    }
}