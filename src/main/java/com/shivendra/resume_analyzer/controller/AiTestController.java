package com.shivendra.resume_analyzer.controller;

import com.shivendra.resume_analyzer.service.AiClientService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
public class AiTestController {

    private final AiClientService aiClientService;

    public AiTestController(AiClientService aiClientService) {
        this.aiClientService = aiClientService;
    }

    @GetMapping("/test")
    public String testAi(@RequestParam String prompt) {
        return aiClientService.askAi(prompt);
    }
}