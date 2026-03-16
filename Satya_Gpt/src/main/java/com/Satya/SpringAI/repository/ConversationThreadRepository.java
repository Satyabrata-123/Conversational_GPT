package com.Satya.SpringAI.repository;

import com.Satya.SpringAI.entity.ConversationThread;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationThreadRepository extends JpaRepository<ConversationThread, Long> {
    
    Optional<ConversationThread> findByThreadId(String threadId);
    
    @Query("SELECT ct FROM ConversationThread ct ORDER BY ct.updatedAt DESC")
    List<ConversationThread> findAllOrderByUpdatedAtDesc();
    
    boolean existsByThreadId(String threadId);
}