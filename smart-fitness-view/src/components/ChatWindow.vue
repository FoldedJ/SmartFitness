<template>
  <div class="chat-window" :class="{ 'chat-window-collapsed': collapsed }">
    <div class="chat-header" @click="toggleCollapse">
      <span>智能助手</span>
      <i :class="collapsed ? 'el-icon-arrow-up' : 'el-icon-arrow-down'"></i>
    </div>
    <div v-show="!collapsed" class="chat-body">
      <div class="chat-messages" ref="messagesContainer">
        <div v-for="(message, index) in messages" :key="index" class="message" :class="message.type">
          <div class="message-content">{{ message.content }}</div>
        </div>
      </div>
      <div class="chat-input">
        <el-input
          v-model="inputMessage"
          placeholder="请输入您的问题..."
          @keyup.enter.native="sendMessage"
          :disabled="loading"
        ></el-input>
        <el-button type="primary" icon="el-icon-s-promotion" @click="sendMessage" :loading="loading">发送</el-button>
      </div>
    </div>
  </div>
</template>

<script>
import { executeWorkflow } from '@/api/coze';

export default {
  name: 'ChatWindow',
  props: {
    // 不再需要workflowId属性
  },
  data() {
    return {
      collapsed: true,
      inputMessage: '',
      messages: [
        {
          type: 'received',
          content: '您好！我是您的智能健康助手，有什么可以帮助您的吗？'
        }
      ],
      loading: false
    };
  },
  methods: {
    toggleCollapse() {
      this.collapsed = !this.collapsed;
    },
    async sendMessage() {
      if (!this.inputMessage.trim() || this.loading) return;
      
      // 添加用户消息到聊天窗口
      this.messages.push({
        type: 'sent',
        content: this.inputMessage
      });
      
      const userInput = this.inputMessage;
      this.inputMessage = '';
      this.loading = true;
      
      // 添加加载消息
      const loadingMsgIndex = this.messages.length;
      this.messages.push({
        type: 'received',
        content: '正在思考中...'
      });
      
      try {
        // 调用Coze工作流API
        const response = await executeWorkflow(userInput);
        
        // 在控制台输出完整的响应对象，用于调试
        console.log('收到的完整响应:', response);
        console.log('响应的data字段:', response.data);
        
        // 处理响应数据
        let responseContent = '';
        if (response && response.data && response.data.code === 200) {
          // 直接使用后端返回的数据，不再尝试解析JSON
          responseContent = response.data.data;
          console.log('使用的响应内容:', responseContent);
        } else if (response && response.data && response.data.message) {
          responseContent = response.data.message;
          console.log('使用的错误消息:', responseContent);
        } else {
          responseContent = '收到响应，但无法解析内容';
          console.log('无法解析响应内容');
        }
        
        // 更新加载消息为实际响应
        this.messages.splice(loadingMsgIndex, 1, {
          type: 'received',
          content: responseContent
        });
      } catch (error) {
        // 更新加载消息为错误信息
        this.messages.splice(loadingMsgIndex, 1, {
          type: 'received',
          content: '抱歉，我遇到了一些问题：' + (error.message || '未知错误')
        });
        console.error('执行工作流出错:', error);
      } finally {
        this.loading = false;
        this.$nextTick(() => {
          this.scrollToBottom();
        });
      }
    },
    scrollToBottom() {
      const container = this.$refs.messagesContainer;
      if (container) {
        container.scrollTop = container.scrollHeight;
      }
    }
  },
  updated() {
    this.scrollToBottom();
  }
};
</script>

<style scoped lang="scss">
.chat-window {
  position: fixed;
  bottom: 20px;
  right: 20px;
  width: 350px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
  z-index: 1000;
  transition: all 0.3s ease;
  max-height: 500px;
  display: flex;
  flex-direction: column;
  
  &.chat-window-collapsed {
    max-height: 50px;
  }
}

.chat-header {
  padding: 12px 15px;
  background-color: #409EFF;
  color: white;
  border-radius: 8px 8px 0 0;
  cursor: pointer;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chat-body {
  display: flex;
  flex-direction: column;
  height: 400px;
}

.chat-messages {
  flex: 1;
  padding: 15px;
  overflow-y: auto;
}

.message {
  margin-bottom: 10px;
  max-width: 80%;
  
  &.sent {
    margin-left: auto;
    
    .message-content {
      background-color: #409EFF;
      color: white;
      border-radius: 18px 18px 0 18px;
    }
  }
  
  &.received {
    margin-right: auto;
    
    .message-content {
      background-color: #f2f2f2;
      color: #333;
      border-radius: 18px 18px 18px 0;
    }
  }
}

.message-content {
  padding: 10px 15px;
  word-break: break-word;
  white-space: pre-wrap;
}

.chat-input {
  padding: 10px;
  display: flex;
  border-top: 1px solid #eee;
  
  .el-input {
    margin-right: 10px;
  }
}
</style>