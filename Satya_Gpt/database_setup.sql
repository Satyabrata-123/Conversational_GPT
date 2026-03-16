-- MySQL Database Setup for Satya GPT
-- Run this script to create the database and tables

-- Create database
CREATE DATABASE IF NOT EXISTS satya_gpt_db;
USE satya_gpt_db;

-- Create conversation_threads table
CREATE TABLE IF NOT EXISTS conversation_threads (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    thread_id VARCHAR(255) UNIQUE NOT NULL,
    title VARCHAR(500) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_thread_id (thread_id),
    INDEX idx_updated_at (updated_at)
);

-- Create messages table
CREATE TABLE IF NOT EXISTS messages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    content TEXT NOT NULL,
    role ENUM('USER', 'ASSISTANT') NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    thread_id BIGINT NOT NULL,
    FOREIGN KEY (thread_id) REFERENCES conversation_threads(id) ON DELETE CASCADE,
    INDEX idx_thread_timestamp (thread_id, timestamp)
);

-- Insert sample data (optional)
INSERT INTO conversation_threads (thread_id, title) VALUES 
('sample-thread-1', 'Welcome to Satya GPT'),
('sample-thread-2', 'Java Programming Help');

INSERT INTO messages (content, role, thread_id) VALUES 
('Hello! How can I help you today?', 'ASSISTANT', 1),
('What is Spring Boot?', 'USER', 2),
('Spring Boot is a framework that makes it easy to create stand-alone, production-grade Spring-based applications.', 'ASSISTANT', 2);

-- Show tables
SHOW TABLES;
DESCRIBE conversation_threads;
DESCRIBE messages;