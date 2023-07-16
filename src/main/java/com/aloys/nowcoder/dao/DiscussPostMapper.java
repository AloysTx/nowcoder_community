package com.aloys.nowcoder.dao;

import com.aloys.nowcoder.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DiscussPostMapper {

    // 查询帖子，useId用以显示用户自己的帖子（首页显示社区所有帖子，就不需要该属性，userId为默认值0的时候不拼接），
    // offset和limit用于分页查询
    List<DiscussPost> selectDiscussPosts(@Param("userId") int userId, @Param("offset") int offset, @Param("limit") int limit);

    // 查询帖子数量(不包括已拉黑贴)（表中记录数，也即行数）
    // userId 同上
    int selectDiscussPostRows(int userId);

    int insertDiscussPost(DiscussPost discussPost);

    DiscussPost selectDiscussPostById(int id);

    int updateCommentCount(@Param("id") int id, @Param("commentCount") int commentCount);
}
