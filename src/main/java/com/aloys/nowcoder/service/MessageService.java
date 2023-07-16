package com.aloys.nowcoder.service;

import com.aloys.nowcoder.dao.MessageMapper;
import com.aloys.nowcoder.entity.Message;
import com.aloys.nowcoder.utils.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.Arrays;
import java.util.List;

@Service
public class MessageService {

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    public List<Message> findConversations(int userId, int offset, int limit) {
        return messageMapper.selectConversations(userId, offset, limit);
    }

    public int findConversationCount(int userId) {
        return messageMapper.selectConversationCount(userId);
    }

    public List<Message> findMessages(String conversationId, int offset, int limit) {
        return messageMapper.selectMessages(conversationId, offset, limit);
    }

    public int findMessageCount(String conversationId) {
        return messageMapper.selectMessageCount(conversationId);
    }

    public int findUnreadMessageCount(int userId, String conversationId) {
        return messageMapper.selectUnreadMessageCount(userId, conversationId);
    }

    // 发送私信
    public int addMessage(Message message) {
        message.setContent(HtmlUtils.htmlEscape(message.getContent()));
        message.setContent(sensitiveFilter.filter(message.getContent()));
        return messageMapper.insertMessage(message);
    }

    // 标记私信为已读
    public int readMessages(List<Integer> ids) {
        return messageMapper.updateStatus(ids, 1);
    }

    // 删除某条私信
    public int deleteMessage(int id) {
        return messageMapper.updateStatus(Arrays.asList(id), 2);
    }
}
