package com.Satya.SpringAI.dto;

import com.Satya.SpringAI.entity.Message;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageDto {
    private Long id;
    private String content;
    private Message.MessageRole role;
    private LocalDateTime timestamp;
}