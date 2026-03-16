import { useState, useRef, useEffect } from 'react'
import './App.css'
import { chatIconSVG, userIconSVG, botIconSVG, sendIconSVG, plusIconSVG, menuIconSVG } from './assets/icons'

function App() {
  const [input, setInput] = useState('')
  const [messages, setMessages] = useState([])
  const [loading, setLoading] = useState(false)
  const [sidebarOpen, setSidebarOpen] = useState(false)
  const [conversations, setConversations] = useState([])
  const [currentThreadId, setCurrentThreadId] = useState(null)
  const [currentTitle, setCurrentTitle] = useState('New Conversation')
  const messagesEndRef = useRef(null)
  const textareaRef = useRef(null)
  
  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' })
  }

  useEffect(() => {
    scrollToBottom()
  }, [messages])
  
  useEffect(() => {
    loadConversations()
  }, [])
  
  // Auto-resize textarea
  useEffect(() => {
    const textarea = textareaRef.current
    if (textarea) {
      textarea.style.height = 'auto'
      textarea.style.height = `${textarea.scrollHeight}px`
    }
  }, [input])

  const loadConversations = async () => {
    try {
      const response = await fetch('http://localhost:8080/api/conversations')
      const data = await response.json()
      setConversations(data)
    } catch (error) {
      console.error('Error loading conversations:', error)
    }
  }

  const loadConversation = async (threadId) => {
    try {
      const response = await fetch(`http://localhost:8080/api/conversations/${threadId}`)
      const data = await response.json()
      
      setCurrentThreadId(threadId)
      setCurrentTitle(data.title)
      setMessages(data.messages.map(msg => ({
        role: msg.role.toLowerCase(),
        content: msg.content
      })))
    } catch (error) {
      console.error('Error loading conversation:', error)
    }
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    if (!input.trim()) return

    // Add user message
    const userMessage = { role: 'user', content: input }
    setMessages(prevMessages => [...prevMessages, userMessage])
    const currentInput = input
    setInput('')
    setLoading(true)
    
    // Reset textarea height
    if (textareaRef.current) {
      textareaRef.current.style.height = 'auto'
    }

    try {
      const response = await fetch('http://localhost:8080/api/chat', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          message: currentInput,
          threadId: currentThreadId // Will be null for new conversations
        })
      })
      
      const data = await response.json()

      // Update current thread info (this will set threadId for new conversations)
      setCurrentThreadId(data.threadId)
      setCurrentTitle(data.title)

      // Add bot message
      setMessages(prevMessages => [
        ...prevMessages,
        { role: 'assistant', content: data.response }
      ])
      
      // Reload conversations to update sidebar
      loadConversations()
    } catch (error) {
      console.error('Error fetching response:', error)
      setMessages(prevMessages => [
        ...prevMessages,
        { role: 'assistant', content: 'Sorry, I encountered an error. Please try again.' }
      ])
    }

    setLoading(false)
  }
  
  const handleKeyDown = (e) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault()
      handleSubmit(e)
    }
  }
  
  const toggleSidebar = () => {
    setSidebarOpen(!sidebarOpen)
  }
  
  const startNewChat = () => {
    // Simply reset the UI state - don't create thread until first message
    setMessages([])
    setCurrentThreadId(null)
    setCurrentTitle('New Conversation')
  }

  const deleteConversation = async (threadId, e) => {
    e.stopPropagation()
    try {
      await fetch(`http://localhost:8080/api/conversations/${threadId}`, {
        method: 'DELETE'
      })
      
      // If we're deleting the current conversation, start a new one
      if (threadId === currentThreadId) {
        startNewChat()
      }
      
      loadConversations()
    } catch (error) {
      console.error('Error deleting conversation:', error)
    }
  }

  const formatDate = (dateString) => {
    const date = new Date(dateString)
    const now = new Date()
    const diffTime = Math.abs(now - date)
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24))
    
    if (diffDays === 1) return 'Today'
    if (diffDays === 2) return 'Yesterday'
    if (diffDays <= 7) return `${diffDays - 1} days ago`
    return date.toLocaleDateString()
  }

  return (
    <div className="chat-container">
      <div className={`chat-sidebar ${sidebarOpen ? 'open' : ''}`}>
        <div className="sidebar-toggle" onClick={toggleSidebar} dangerouslySetInnerHTML={{ __html: menuIconSVG }} />
        <div className="sidebar-header">
          <div className="logo-container">
            <div className="logo" dangerouslySetInnerHTML={{ __html: chatIconSVG }} />
            <div className="app-title">Satya GPT</div>
          </div>
          <button className="new-chat-btn" onClick={startNewChat}>
            <span dangerouslySetInnerHTML={{ __html: plusIconSVG }} />
            New Conversation
          </button>
        </div>
        <div className="sidebar-history">
          {conversations.map((conversation) => (
            <div 
              key={conversation.threadId} 
              className={`conversation-item ${conversation.threadId === currentThreadId ? 'active' : ''}`}
              onClick={() => loadConversation(conversation.threadId)}
            >
              <div className="conversation-title">{conversation.title}</div>
              <div className="conversation-meta">
                <span className="conversation-date">{formatDate(conversation.updatedAt)}</span>
                <span className="conversation-count">{conversation.messageCount} messages</span>
                <button 
                  className="delete-btn"
                  onClick={(e) => deleteConversation(conversation.threadId, e)}
                  title="Delete conversation"
                >
                  ×
                </button>
              </div>
            </div>
          ))}
        </div>
        <div className="sidebar-footer">
          <div className="user-info">
            <div className="user-icon">
              <div dangerouslySetInnerHTML={{ __html: userIconSVG }} />
            </div>
            <span>Your Account</span>
          </div>
        </div>
      </div>

      <div className="chat-main">
        <div className="chat-header">
          <h2>{currentTitle}</h2>
        </div>

        <div className="messages-container">
          {messages.length === 0 ? (
            <div className="welcome-container">
              <div className="welcome-logo" dangerouslySetInnerHTML={{ __html: chatIconSVG }} />
              <h1>Welcome to Satya GPT</h1>
              <p>Your intelligent assistant powered by Spring AI. Ask me anything about programming, Java, technology, or any topic you'd like to explore.</p>
            </div>
          ) : (
            messages.map((message, index) => (
              <div key={index} className={`message ${message.role}`}>
                <div className="message-avatar">
                  <div dangerouslySetInnerHTML={{ 
                    __html: message.role === 'user' ? userIconSVG : botIconSVG 
                  }} />
                </div>
                <div className="message-content">
                  {message.content}
                </div>
              </div>
            ))
          )}
          {loading && (
            <div className="message assistant">
              <div className="message-avatar">
                <div dangerouslySetInnerHTML={{ __html: botIconSVG }} />
              </div>
              <div className="message-content">
                <div className="loading-dots">
                  <span></span>
                  <span></span>
                  <span></span>
                </div>
              </div>
            </div>
          )}
          <div ref={messagesEndRef} />
        </div>

        <div className="input-container">
          <form onSubmit={handleSubmit}>
            <textarea
              ref={textareaRef}
              value={input}
              onChange={(e) => setInput(e.target.value)}
              onKeyDown={handleKeyDown}
              placeholder="Ask anything..."
              className="message-input"
              rows="1"
            />
            <button 
              type="submit" 
              className="send-button"
              disabled={!input.trim() || loading}
            >
              <div dangerouslySetInnerHTML={{ __html: sendIconSVG }} />
            </button>
          </form>
        </div>
      </div>
    </div>
  )
}

export default App