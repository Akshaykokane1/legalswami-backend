package com.legalswami.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ChatService {

    @Value("${openrouter.key:}")
    private String openRouterKey;

    public String chat(String message) {
        if (openRouterKey == null || openRouterKey.isEmpty()) {
            return "Backend is running, but OPENROUTER_KEY is not configured yet. Please add it as an environment variable.";
        }

        // Placeholder: here you would call OpenRouter using HTTP client.
        // To keep this template simple and dependency‑light, we just echo the input.
        return "You said: " + message + "\n\n"
                + "(Connect this backend to OpenRouter to get real AI answers.)";
    }
}
