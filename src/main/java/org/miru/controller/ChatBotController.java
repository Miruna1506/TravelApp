package org.miru.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ChatBotController {

    @GetMapping("/api/chatbot/questions")
    public List<String> getChatbotQuestions() {
        return List.of(
                "Which city would you like to visit?",
                "How many days will your trip last?",
                "What is your approximate budget?",
                "What are your interests? For example: history, food, nature, shopping.",
                "Would you like restaurant recommendations near the places you visit?"
        );
    }
}