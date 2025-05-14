<template>
  <div id="app">
    <header class="app-header">
      <h1>中医机器人智能问诊系统</h1>
    </header>
    <main class="consultation-container">
      <div class="chat-window">
        <div class="message-list" ref="messageList">
          <div v-for="(msg, index) in messages" :key="index" :class="['message', msg.sender]">
            <div class="message-content">{{ msg.content }}</div>
            <div class="message-time">{{ msg.time }}</div>
          </div>
        </div>
        <div class="input-area">
          <textarea 
            v-model="userInput" 
            placeholder="请描述您的症状..." 
            @keyup.enter="sendMessage"
          ></textarea>
          <button @click="sendMessage" :disabled="isProcessing">发送</button>
        </div>
      </div>
      <div class="diagnosis-panel" v-if="diagnosis">
        <h3>辨证分析结果</h3>
        <div class="result-card">
          <div class="result-item">
            <span class="item-label">体质类型:</span>
            <span class="item-value">{{ diagnosis.bodyType }}</span>
          </div>
          <div class="result-item">
            <span class="item-label">主要证型:</span>
            <span class="item-value">{{ diagnosis.mainSyndrome }}</span>
          </div>
          <div class="result-item diagnosis-suggestions">
            <span class="item-label">调理建议:</span>
            <div class="suggestions-list">
              <p v-for="(suggestion, idx) in diagnosis.suggestions" :key="idx">
                {{ suggestion }}
              </p>
            </div>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script>
export default {
  name: 'App',
  data() {
    return {
      userInput: '',
      messages: [
        {
          content: '您好，我是中医智能问诊机器人。请描述您的症状，我将为您提供中医辨证分析和调理建议。',
          sender: 'robot',
          time: this.formatTime(new Date())
        }
      ],
      isProcessing: false,
      diagnosis: null,
      socket: null
    }
  },
  mounted() {
    this.initWebSocket();
  },
  methods: {
    initWebSocket() {
      const apiGateway = process.env.VUE_APP_API_GATEWAY || 'ws://localhost:8000/ws';
      this.socket = new WebSocket(apiGateway);
      
      this.socket.onopen = () => {
        console.log('WebSocket连接已建立');
      };
      
      this.socket.onmessage = (event) => {
        try {
          const data = JSON.parse(event.data);
          
          if (data.messageType === 'chat') {
            this.messages.push({
              content: data.content,
              sender: 'robot',
              time: this.formatTime(new Date())
            });
            this.scrollToBottom();
          } else if (data.messageType === 'diagnosis') {
            this.diagnosis = data.diagnosis;
          }
          
          this.isProcessing = false;
        } catch (error) {
          console.error('解析消息失败:', error);
        }
      };
      
      this.socket.onerror = (error) => {
        console.error('WebSocket错误:', error);
        this.messages.push({
          content: '连接出错，请稍后再试。',
          sender: 'system',
          time: this.formatTime(new Date())
        });
        this.isProcessing = false;
      };
      
      this.socket.onclose = () => {
        console.log('WebSocket连接已关闭');
        setTimeout(() => {
          this.initWebSocket();
        }, 3000);
      };
    },
    sendMessage() {
      if (!this.userInput.trim() || this.isProcessing) return;
      
      const message = {
        content: this.userInput,
        sender: 'user',
        time: this.formatTime(new Date())
      };
      
      this.messages.push(message);
      this.isProcessing = true;
      
      if (this.socket && this.socket.readyState === WebSocket.OPEN) {
        this.socket.send(JSON.stringify({
          type: 'message',
          content: this.userInput
        }));
      } else {
        this.messages.push({
          content: '网络连接异常，请刷新页面重试。',
          sender: 'system',
          time: this.formatTime(new Date())
        });
        this.isProcessing = false;
      }
      
      this.userInput = '';
      this.scrollToBottom();
    },
    formatTime(date) {
      const hours = date.getHours().toString().padStart(2, '0');
      const minutes = date.getMinutes().toString().padStart(2, '0');
      return `${hours}:${minutes}`;
    },
    scrollToBottom() {
      this.$nextTick(() => {
        const messageList = this.$refs.messageList;
        messageList.scrollTop = messageList.scrollHeight;
      });
    }
  }
}
</script>

<style>
* {
  box-sizing: border-box;
  margin: 0;
  padding: 0;
}

body {
  font-family: 'Microsoft YaHei', sans-serif;
  background-color: #f5f5f5;
  color: #333;
}

#app {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

.app-header {
  background-color: #4caf50;
  color: white;
  text-align: center;
  padding: 1rem;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.consultation-container {
  display: flex;
  flex: 1;
  padding: 1rem;
  gap: 1rem;
  max-width: 1200px;
  margin: 0 auto;
  width: 100%;
}

.chat-window {
  display: flex;
  flex-direction: column;
  flex: 2;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
  overflow: hidden;
}

.message-list {
  flex: 1;
  padding: 1rem;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.message {
  max-width: 80%;
  padding: 0.75rem 1rem;
  border-radius: 18px;
  position: relative;
}

.message.user {
  align-self: flex-end;
  background-color: #dcf8c6;
  border-bottom-right-radius: 4px;
}

.message.robot {
  align-self: flex-start;
  background-color: #f0f0f0;
  border-bottom-left-radius: 4px;
}

.message.system {
  align-self: center;
  background-color: #ffe0e0;
  border-radius: 8px;
  font-size: 0.9rem;
  color: #d32f2f;
}

.message-time {
  font-size: 0.7rem;
  color: #999;
  margin-top: 0.25rem;
  text-align: right;
}

.input-area {
  display: flex;
  padding: 1rem;
  background-color: #f9f9f9;
  border-top: 1px solid #eee;
}

.input-area textarea {
  flex: 1;
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  resize: none;
  height: 80px;
  font-family: inherit;
}

.input-area button {
  padding: 0 1.5rem;
  margin-left: 0.5rem;
  background-color: #4caf50;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-weight: bold;
  transition: background-color 0.2s;
}

.input-area button:hover {
  background-color: #388e3c;
}

.input-area button:disabled {
  background-color: #9e9e9e;
  cursor: not-allowed;
}

.diagnosis-panel {
  flex: 1;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
  padding: 1rem;
}

.diagnosis-panel h3 {
  color: #4caf50;
  border-bottom: 1px solid #eee;
  padding-bottom: 0.5rem;
  margin-bottom: 1rem;
}

.result-card {
  background-color: #f9f9f9;
  border-radius: 8px;
  padding: 1rem;
}

.result-item {
  margin-bottom: 1rem;
}

.item-label {
  font-weight: bold;
  color: #555;
  display: block;
  margin-bottom: 0.25rem;
}

.item-value {
  color: #333;
}

.diagnosis-suggestions {
  margin-top: 1rem;
}

.suggestions-list {
  margin-top: 0.5rem;
}

.suggestions-list p {
  margin-bottom: 0.5rem;
  line-height: 1.5;
}

@media (max-width: 768px) {
  .consultation-container {
    flex-direction: column;
  }
  
  .message {
    max-width: 90%;
  }
}
</style> 