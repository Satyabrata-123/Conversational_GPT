package com.Satya.SpringAI.Controller;

import com.Satya.SpringAI.dto.ChatRequest;
import com.Satya.SpringAI.dto.ChatResponse;
import com.Satya.SpringAI.dto.ChatResult;
import com.Satya.SpringAI.dto.ConversationThreadDto;
import com.Satya.SpringAI.service.ConversationService;
import com.Satya.SpringAI.service.GeminiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AiController {
    
    private final GeminiService geminiService;
    private final ConversationService conversationService;

    // Legacy endpoint for backward compatibility
    @GetMapping("/{message}")
    public String getResponse(@PathVariable String message) {
        return geminiService.getAnswer(message);
    }

    // New chat endpoint with conversation history
    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request) {
        ChatResult result = geminiService.getAnswerWithHistory(request.getMessage(), request.getThreadId());
        
        ConversationThreadDto threadDto = conversationService.getConversationWithMessages(result.getThreadId());
        
        return ResponseEntity.ok(new ChatResponse(result.getResponse(), result.getThreadId(), threadDto.getTitle()));
    }

    // Get all conversations
    @GetMapping("/conversations")
    public ResponseEntity<List<ConversationThreadDto>> getAllConversations() {
        List<ConversationThreadDto> conversations = conversationService.getAllConversations();
        return ResponseEntity.ok(conversations);
    }

    // Get specific conversation with messages
    @GetMapping("/conversations/{threadId}")
    public ResponseEntity<ConversationThreadDto> getConversation(@PathVariable String threadId) {
        ConversationThreadDto conversation = conversationService.getConversationWithMessages(threadId);
        return ResponseEntity.ok(conversation);
    }

    // Create new conversation
    @PostMapping("/conversations")
    public ResponseEntity<String> createNewConversation() {
        String threadId = conversationService.createNewThread();
        return ResponseEntity.ok(threadId);
    }

    // Delete conversation
    @DeleteMapping("/conversations/{threadId}")
    public ResponseEntity<Void> deleteConversation(@PathVariable String threadId) {
        conversationService.deleteConversation(threadId);
        return ResponseEntity.ok().build();
    }
}
