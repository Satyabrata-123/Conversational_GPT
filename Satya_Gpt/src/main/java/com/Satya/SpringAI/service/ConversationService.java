package com.Satya.SpringAI.service;

import com.Satya.SpringAI.dto.ConversationThreadDto;
import com.Satya.SpringAI.dto.MessageDto;
import com.Satya.SpringAI.entity.ConversationThread;
import com.Satya.SpringAI.entity.Message;
import com.Satya.SpringAI.repository.ConversationThreadRepository;
import com.Satya.SpringAI.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConversationService {

    private final ConversationThreadRepository threadRepository;
    private final MessageRepository messageRepository;

    public List<ConversationThreadDto> getAllConversations() {
        List<ConversationThread> threads = threadRepository.findAllOrderByUpdatedAtDesc();
        
        return threads.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public ConversationThreadDto getConversationWithMessages(String threadId) {
        ConversationThread thread = threadRepository.findByThreadId(threadId)
                .orElseThrow(() -> new RuntimeException("Thread not found: " + threadId));
        
        List<Message> messages = messageRepository.findByThreadOrderByTimestampAsc(thread);
        
        ConversationThreadDto dto = convertToDto(thread);
        dto.setMessages(messages.stream()
                .map(this::convertMessageToDto)
                .collect(Collectors.toList()));
        
        return dto;
    }

    public String createNewThread() {
        String threadId = UUID.randomUUID().toString();
        
        ConversationThread thread = new ConversationThread();
        thread.setThreadId(threadId);
        thread.setTitle("New Conversation");
        
        threadRepository.save(thread);
        
        log.info("Created new thread: {}", threadId);
        return threadId;
    }

    @Transactional
    public void deleteConversation(String threadId) {
        ConversationThread thread = threadRepository.findByThreadId(threadId)
                .orElseThrow(() -> new RuntimeException("Thread not found: " + threadId));
        
        threadRepository.delete(thread);
        log.info("Deleted thread: {}", threadId);
    }

    private ConversationThreadDto convertToDto(ConversationThread thread) {
        long messageCount = messageRepository.countByThreadId(thread.getThreadId());
        
        return new ConversationThreadDto(
                thread.getThreadId(),
                thread.getTitle(),
                thread.getCreatedAt(),
                thread.getUpdatedAt(),
                (int) messageCount,
                null // Messages will be loaded separately if needed
        );
    }

    private MessageDto convertMessageToDto(Message message) {
        return new MessageDto(
                message.getId(),
                message.getContent(),
                message.getRole(),
                message.getTimestamp()
        );
    }
}