package com.aloys.nowcoder.service;

import com.aloys.nowcoder.dao.DiscussPostMapper;
import com.aloys.nowcoder.entity.DiscussPost;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiscussPostService {

    @Autowired
    private DiscussPostMapper discussPostMapper;

    public List<DiscussPost> findDiscussPosts(@Param("userId") int userId, @Param("offset") int offset, @Param("limit") int limit) {
        return discussPostMapper.selectDiscussPost(userId, offset, limit);
    }

    public int findDiscussPostRows(int userId) {
        return discussPostMapper.selectDiscussPostRows(userId);
    }

}
