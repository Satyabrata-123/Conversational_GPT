package com.Satya.SpringAI.repository;

import com.Satya.SpringAI.entity.Message;
import com.Satya.SpringAI.entity.ConversationThread;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    
    List<Message> findByThreadOrderByTimestampAsc(ConversationThread thread);
    
    @Query("SELECT m FROM Message m WHERE m.thread.threadId = :threadId ORDER BY m.timestamp ASC")
    List<Message> findByThreadIdOrderByTimestampAsc(@Param("threadId") String threadId);
    
    @Query("SELECT COUNT(m) FROM Message m WHERE m.thread.threadId = :threadId")
    long countByThreadId(@Param("threadId") String threadId);
}