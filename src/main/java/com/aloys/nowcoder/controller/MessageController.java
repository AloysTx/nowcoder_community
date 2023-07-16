package com.aloys.nowcoder.controller;

import com.aloys.nowcoder.entity.Message;
import com.aloys.nowcoder.entity.Page;
import com.aloys.nowcoder.entity.User;
import com.aloys.nowcoder.service.MessageService;
import com.aloys.nowcoder.service.UserService;
import com.aloys.nowcoder.utils.CommonUtils;
import com.aloys.nowcoder.utils.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
public class MessageController {
    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;


    // 会话（私信）列表显示
    @GetMapping("/conversation/list")
    public String getConversationList(Model model, Page page) {
        User user = hostHolder.getUser();
        // 分页信息
        page.setLimit(5);
        page.setPath("/conversation/list");
        page.setRows(messageService.findConversationCount(user.getId()));

        // 会话列表
        List<Message> messages = messageService.findConversations(user.getId(), page.getOffset(), page.getLimit());
        List<Map<String, Object>> conversations = new ArrayList<>();
        if(messages != null) {
            for(Message message: messages) {
                Map<String, Object> map = new HashMap<>();
                map.put("message", message);
                // 获取指定会话的未读消息数
                map.put("messageCount", messageService.findMessageCount(message.getConversationId()));
                map.put("unreadCount", messageService.findUnreadMessageCount(user.getId(), message.getConversationId()));
                // 获取通信对方用户的 id
                int targetId = user.getId() == message.getFromId() ? message.getToId() : message.getFromId();
                map.put("target", userService.findUserById(targetId));

                conversations.add(map);
            }
        }

        model.addAttribute("conversations", conversations);

        // 查询总的未读消息
        int totalUnreadMessage = messageService.findUnreadMessageCount(user.getId(), null);
        model.addAttribute("totalUnread", totalUnreadMessage);

        return "/site/letter";
    }

    @GetMapping("/conversation/detail/{conversationId}")
    public String getConversationDetail(@PathVariable("conversationId") String conversationId, Page page, Model model) {
        // 设置分页信息
        page.setLimit(5);
        page.setPath("/conversation/detail/" + conversationId);
        page.setRows(messageService.findMessageCount(conversationId));

        // 私信列表
        List<Message> messageList = messageService.findMessages(conversationId, page.getOffset(), page.getLimit());

        List<Map<String, Object>> messages = new ArrayList<>();
        if(messageList != null) {
            for(Message message: messageList) {
                Map<String, Object> map = new HashMap<>();
                map.put("message", message);
                // 发送方
                map.put("from", userService.findUserById(message.getFromId()));
                messages.add(map);
            }
        }
        model.addAttribute("messages", messages);

        // 获取通信目标（对方）的用户信息
        model.addAttribute("target", getMessageTarget(conversationId));

        // 设置已读
        List<Integer> unreadMessageIds = getUnreadMessageIds(messageList);
        if (!unreadMessageIds.isEmpty()) {
            messageService.readMessages(unreadMessageIds);
        }
        return "/site/letter-detail";
    }

    public User getMessageTarget(String conversationId) {
        String[] ids = conversationId.split("_");
        int id0 = Integer.parseInt(ids[0]);
        int id1 = Integer.parseInt(ids[1]);
        return hostHolder.getUser().getId() == id0 ? userService.findUserById(id1) : userService.findUserById(id0);
    }

    // 返回当前会话中所有未读消息的 id
    public List<Integer> getUnreadMessageIds(List<Message> messageList) {
        List<Integer> list = new ArrayList<>();
        if(messageList != null) {
            for(Message message: messageList) {
                if(hostHolder.getUser().getId() == message.getToId() && message.getStatus() == 0) {
                    list.add(message.getId());
                }
            }
        }
        return list;
    }


    // 发送私信
    @PostMapping("/conversation/send")
    @ResponseBody
    public String sendMessage(String toName, String content) {
        User to = userService.findUserByName(toName);
        if (to == null) {
            return CommonUtils.getJsonString(1, "目标用户不存在");
        }

        Message message = new Message();
        message.setFromId(hostHolder.getUser().getId());
        message.setToId(to.getId());
        // 拼接 conversationId
        if (message.getFromId() < message.getToId()) {
            message.setConversationId(message.getFromId() + "_" + message.getToId());
        } else {
            message.setConversationId(message.getToId() + "_" + message.getFromId());
        }
        message.setContent(content);
        message.setCreateTime(new Date());
        messageService.addMessage(message);

        return CommonUtils.getJsonString(0);
    }

    // 删除私信
    @RequestMapping(path = "/conversation/delete")
    @ResponseBody
    public String deleteMessage(int id) {
        messageService.deleteMessage(id);
        return CommonUtils.getJsonString(0);
    }
}
