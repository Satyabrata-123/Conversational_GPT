package com.Satya.SpringAI.service;

import com.Satya.SpringAI.dto.ChatResult;
import com.Satya.SpringAI.entity.ConversationThread;
import com.Satya.SpringAI.entity.Message;
import com.Satya.SpringAI.repository.ConversationThreadRepository;
import com.Satya.SpringAI.repository.MessageRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class GeminiService {

    private final ChatClient chatClient;
    private final ConversationThreadRepository threadRepository;
    private final MessageRepository messageRepository;

    @Transactional
    public ChatResult getAnswerWithHistory(String question, String threadId) {
        log.info("Processing question for thread: {}", threadId);
        
        // Get or create conversation thread
        ConversationThread thread = getOrCreateThread(threadId);
        String actualThreadId = thread.getThreadId();
        
        // Get conversation history
        List<Message> history = messageRepository.findByThreadOrderByTimestampAsc(thread);
        
        // Build chat client request with history
        var requestBuilder = chatClient.prompt();
        
        // Add conversation history
        for (Message msg : history) {
            if (msg.getRole() == Message.MessageRole.USER) {
                requestBuilder = requestBuilder.messages(new UserMessage(msg.getContent()));
            } else {
                requestBuilder = requestBuilder.messages(new AssistantMessage(msg.getContent()));
            }
        }
        
        // Add current question
        String response = requestBuilder
                .user(question)
                .call()
                .content();
        
        // Save user message
        saveMessage(thread, question, Message.MessageRole.USER);
        
        // Save assistant response
        saveMessage(thread, response, Message.MessageRole.ASSISTANT);
        
        // Update thread title if it's the first message
        if (history.isEmpty()) {
            updateThreadTitle(thread, question);
        }
        
        log.info("Response generated for thread: {}", actualThreadId);
        return new ChatResult(response, actualThreadId);
    }

    public String getAnswer(String question) {
        String response = chatClient
                .prompt()
                .user(question)
                .call()
                .content();
        
        log.info("Response from Gemini: {}", response);
        return response;
    }
    
    private ConversationThread getOrCreateThread(String threadId) {
        final String finalThreadId = (threadId == null || threadId.trim().isEmpty()) 
            ? UUID.randomUUID().toString() 
            : threadId;
        
        return threadRepository.findByThreadId(finalThreadId)
                .orElseGet(() -> {
                    ConversationThread newThread = new ConversationThread();
                    newThread.setThreadId(finalThreadId);
                    newThread.setTitle("New Conversation");
                    return threadRepository.save(newThread);
                });
    }
    
    private void saveMessage(ConversationThread thread, String content, Message.MessageRole role) {
        Message message = new Message();
        message.setThread(thread);
        message.setContent(content);
        message.setRole(role);
        messageRepository.save(message);
    }
    
    private void updateThreadTitle(ConversationThread thread, String firstMessage) {
        String title = firstMessage.length() > 50 
            ? firstMessage.substring(0, 47) + "..." 
            : firstMessage;
        thread.setTitle(title);
        threadRepository.save(thread);
    }
}