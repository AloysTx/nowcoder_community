package com.aloys.nowcoder.controller;

import com.aloys.nowcoder.entity.Comment;
import com.aloys.nowcoder.entity.DiscussPost;
import com.aloys.nowcoder.entity.Page;
import com.aloys.nowcoder.entity.User;
import com.aloys.nowcoder.service.CommentService;
import com.aloys.nowcoder.service.DiscussPostService;
import com.aloys.nowcoder.service.UserService;
import com.aloys.nowcoder.utils.CommonUtils;
import com.aloys.nowcoder.utils.HostHolder;
import com.aloys.nowcoder.utils.NowCoderConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController implements NowCoderConstants {
    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private HostHolder hostHolder;

    @PostMapping(path = "/add")
    @ResponseBody
    public String addDiscussPost(String title, String content) {
        User user = hostHolder.getUser();
        if (user == null) {
            // 403 表示无权限
            return CommonUtils.getJsonString(403, "请登录！");
        }

        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle(title);
        post.setContent(content);
        post.setCreateTime(new Date());
        discussPostService.addDiscussPost(post);

//        //触发发帖事件
//        Event event = new Event()
//                .setTopic(TOPIC_PUBLISH)
//                .setUserId(user.getId())
//                .setEntityType(ENTITY_TYPE_POST)
//                .setEntityId(post.getId());
//        eventProducer.fireEvent(event);
//
//        // 计算帖子分数
//        String redisKey = RedisKeyUtil.getPostScoreKey();
//        redisTemplate.opsForSet().add(redisKey, post.getId());

        // 报错将会在未来统一处理
        return CommonUtils.getJsonString(0, "发布成功！");
    }

    // 这里不用关联查询，而是先查一次 post， 再查一次 user，共两次查询
    // 因为关联查询会造成耦合，虽然查询两次效率会有影响，但后期会利用 redis 处理
    @GetMapping("/detail/{postId}")
    public String getDiscussPost(@PathVariable("postId") int postId, Model model, Page page) {
        // 先查询帖子
        DiscussPost discussPost = discussPostService.findDiscussPostById(postId);
        model.addAttribute("post", discussPost);
        // 再查询帖子作者
        User user = userService.findUserById(discussPost.getUserId());
        model.addAttribute("user", user);

        // 设置评论分页
        page.setLimit(5);
        page.setPath("/discuss/detail/" + postId);
        page.setRows(discussPost.getCommentCount());

        // 评论：帖子的评论
        // 回复：给帖子的评论的回复
        // 查询评论
        List<Comment> commentList = commentService.findCommentsByEntity(ENTITY_TYPE_POST, discussPost.getId(),
                page.getOffset(), page.getLimit());

        List<Map<String, Object>> commentVoList = new ArrayList<>();
        if (commentList != null) {
            for (Comment comment: commentList) {
                // 评论Vo
                Map<String, Object> commentVo = new HashMap<>();
                // 评论
                commentVo.put("comment", comment);
                // 作者
                commentVo.put("user", userService.findUserById(comment.getUserId()));

                // 回复列表，不分页，所以 limit 设置为 MXX_VALUE
                List<Comment> replyList = commentService.findCommentsByEntity(ENTITY_TYPE_COMMENT,
                        comment.getId(), 0, Integer.MAX_VALUE);
                // 回复Vo列表
                List<Map<String, Object>> replyVoList = new ArrayList<>();
                if (replyList != null) {
                    for (Comment reply : replyList) {
                        Map<String, Object> replyVo = new HashMap<>();
                        // 回复
                        replyVo.put("reply", reply);
                        // 作者
                        replyVo.put("user", userService.findUserById(reply.getUserId()));


                        // 回复的目标
                        User target = reply.getTargetId() == 0 ? null : userService.findUserById(reply.getTargetId());
                        replyVo.put("target", target);

                        replyVoList.add(replyVo);
                    }
                }
                commentVo.put("replies", replyVoList);

                // 回复数量
                int replyCount = commentService.findCommentCount(ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("replyCount", replyCount);

                commentVoList.add(commentVo);
            }
        }

        model.addAttribute("comments", commentVoList);

        return "site/discuss-detail";
    }
}
