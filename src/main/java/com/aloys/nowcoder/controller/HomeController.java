package com.aloys.nowcoder.controller;

import com.aloys.nowcoder.entity.DiscussPost;
import com.aloys.nowcoder.entity.Page;
import com.aloys.nowcoder.entity.User;
import com.aloys.nowcoder.service.DiscussPostService;
import com.aloys.nowcoder.service.LikeService;
import com.aloys.nowcoder.service.UserService;
import com.aloys.nowcoder.utils.NowCoderConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.*;

@Controller
public class HomeController implements NowCoderConstants {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

//    @RequestMapping(path = "/index", method = RequestMethod.GET)
    @GetMapping("/index")
    public String getIndexPage(Model model, Page page) {
        // 方法调用前，SpringMVC 会自动实例化 Model 和 Page，并将 Page 注入 Model，因此不需要再 addAttribute
        // Model 中的变量可以被 thymeleaf 访问，实现动态页面

        // rows 和 部分path 由服务器获取，其余要么从数据库读取，要么来自客户端
        page.setRows(discussPostService.findDiscussPostRows(0));
        page.setPath("/index");

        List<DiscussPost> list = discussPostService.findDiscussPosts(0, page.getOffset(), page.getLimit());
        // 封装 DiscussionPost 对象和相应 User对象
        List<Map<String, Object>> discussPosts = new ArrayList<>();
        if(list != null) {
            for(DiscussPost post: list) {
                Map<String, Object> map = new HashMap<>();
                map.put("post", post);
                User user = userService.findUserById(post.getUserId());
                map.put("user", user);

                // 获取贴子点赞数
                long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId());
                map.put("likeCount", likeCount);

                discussPosts.add(map);
            }
        }
        model.addAttribute("discussionPosts", discussPosts);
        return "/index";
    }
}
