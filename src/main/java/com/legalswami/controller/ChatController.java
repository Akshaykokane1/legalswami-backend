package com.legalswami.controller;

import com.legalswami.dto.ChatRequest;
import com.legalswami.dto.ChatResponse;
import com.legalswami.service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request) {
        String reply = chatService.chat(request.getMessage());
        return ResponseEntity.ok(new ChatResponse(reply));
    }

    @GetMapping("/health")
    public String health() {
        return "OK";
    }
}
