package com.aloys.nowcoder.dao;

import com.aloys.nowcoder.entity.Message;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MessageMapper {
    List<Message> selectConversations(@Param("userId") int userId, @Param("offset") int offset, @Param("limit") int limit);

    int selectConversationCount(int userId);

    List<Message> selectMessages(@Param("conversationId") String conversationId,
                                 @Param("offset") int offset, @Param("limit") int limit);

    int selectMessageCount(String conversationId);

    int selectUnreadMessageCount(@Param("userId") int userId, @Param("conversationId") String conversationId);

    // 发送一个消息
    int insertMessage(Message message);

    // 更改消息状态
    int updateStatus(@Param("ids") List<Integer> ids, @Param("status") int status);


}
