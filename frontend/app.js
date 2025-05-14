document.addEventListener('DOMContentLoaded', function() {
    // Set the initial timestamp
    const initialTime = document.getElementById('initial-time');
    initialTime.textContent = formatTime(new Date());
    
    // DOM elements
    const messageList = document.getElementById('messageList');
    const userInput = document.getElementById('userInput');
    const sendButton = document.getElementById('sendButton');
    const diagnosisPanel = document.getElementById('diagnosisPanel');
    const bodyTypeEl = document.getElementById('bodyType');
    const mainSyndromeEl = document.getElementById('mainSyndrome');
    const suggestionsListEl = document.getElementById('suggestionsList');
    
    // Variables
    let isProcessing = false;
    
    // Event listeners
    sendButton.addEventListener('click', sendMessage);
    userInput.addEventListener('keypress', function(e) {
        if (e.key === 'Enter' && !e.shiftKey) {
            e.preventDefault();
            sendMessage();
        }
    });

    // Function to send messages
    function sendMessage() {
        const message = userInput.value.trim();
        if (!message || isProcessing) return;
        
        // Add user message to chat
        addMessage(message, 'user');
        
        // Clear input and show processing
        userInput.value = '';
        isProcessing = true;
        sendButton.disabled = true;
        
        // Simulate API call for demo
        setTimeout(() => {
            // First acknowledge message
            addMessage('正在为您分析，请稍候...', 'robot');
            
            // Simulate analysis
            setTimeout(() => {
                // Get analysis result (simulation)
                const result = analyzeSymptoms(message);
                
                // Display diagnosis panel
                diagnosisPanel.style.display = 'block';
                bodyTypeEl.textContent = result.bodyType;
                mainSyndromeEl.textContent = result.mainSyndrome;
                
                // Add suggestions
                suggestionsListEl.innerHTML = '';
                result.suggestions.forEach((suggestion, index) => {
                    const p = document.createElement('p');
                    p.textContent = `${index + 1}. ${suggestion}`;
                    suggestionsListEl.appendChild(p);
                });
                
                // Send summary message
                const summary = `根据您的症状描述，您的体质类型可能是【${result.bodyType}】，主要证型为【${result.mainSyndrome}】。\n\n建议：\n${result.suggestions.map((s, i) => `${i + 1}. ${s}`).join('\n')}`;
                addMessage(summary, 'robot');
                
                // Enable input again
                isProcessing = false;
                sendButton.disabled = false;
            }, 3000);
        }, 1000);
    }

    // Function to add messages to the chat
    function addMessage(content, sender) {
        const messageDiv = document.createElement('div');
        messageDiv.className = `message ${sender}`;
        
        const contentDiv = document.createElement('div');
        contentDiv.className = 'message-content';
        contentDiv.textContent = content;
        
        const timeDiv = document.createElement('div');
        timeDiv.className = 'message-time';
        timeDiv.textContent = formatTime(new Date());
        
        messageDiv.appendChild(contentDiv);
        messageDiv.appendChild(timeDiv);
        
        messageList.appendChild(messageDiv);
        
        // Scroll to bottom
        messageList.scrollTop = messageList.scrollHeight;
    }
    
    // Function to format time
    function formatTime(date) {
        const hours = date.getHours().toString().padStart(2, '0');
        const minutes = date.getMinutes().toString().padStart(2, '0');
        return `${hours}:${minutes}`;
    }
    
    // Simulated symptom analysis (replace with actual API call)
    function analyzeSymptoms(text) {
        if (text.includes('头痛')) {
            return {
                bodyType: '阳虚',
                mainSyndrome: '肝阳上亢',
                suggestions: [
                    '注意休息，避免过度劳累',
                    '建议食用菊花茶，有清肝明目的功效',
                    '可以按摩太阳穴，缓解头痛症状'
                ]
            };
        } else if (text.includes('失眠')) {
            return {
                bodyType: '阴虚',
                mainSyndrome: '心脾两虚',
                suggestions: [
                    '晚上避免饮用咖啡、茶等刺激性饮品',
                    '睡前可以泡脚，有助于安神',
                    '可以适量食用小米粥，有养心安神的作用',
                    '建议保持规律的作息时间'
                ]
            };
        } else if (text.includes('腹泻')) {
            return {
                bodyType: '脾虚',
                mainSyndrome: '脾胃虚弱',
                suggestions: [
                    '饮食宜清淡，少食生冷食物',
                    '可以食用白米粥，帮助恢复肠胃功能',
                    '注意保暖，避免着凉',
                    '适量运动，增强脾胃功能'
                ]
            };
        } else {
            return {
                bodyType: '气虚',
                mainSyndrome: '气血不足',
                suggestions: [
                    '保持充足的睡眠，避免过度疲劳',
                    '可以食用黄芪炖鸡汤，有补气血的功效',
                    '适量运动，如太极拳、散步等',
                    '保持心情舒畅，避免精神压力过大'
                ]
            };
        }
    }
}); 