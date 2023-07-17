package com.aloys.nowcoder.controller;

import com.aloys.nowcoder.annotation.LoginRequired;
import com.aloys.nowcoder.entity.Page;
import com.aloys.nowcoder.entity.User;
import com.aloys.nowcoder.service.FollowService;
import com.aloys.nowcoder.service.UserService;
import com.aloys.nowcoder.utils.CommonUtils;
import com.aloys.nowcoder.utils.HostHolder;
import com.aloys.nowcoder.utils.NowCoderConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
public class FollowController implements NowCoderConstants {
    @Autowired
    private FollowService followService;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @PostMapping("/follow")
    @ResponseBody
    @LoginRequired
    public String follow(int entityType, int entityId) {
        User user = hostHolder.getUser();

        followService.follow(user.getId(), entityType, entityId);

        return CommonUtils.getJsonString(0, "已关注");
    }

    @PostMapping("/unfollow")
    @ResponseBody
    public String unfollow(int entityType, int entityId) {
        User user = hostHolder.getUser();

        followService.unfollow(user.getId(), entityType, entityId);

        return CommonUtils.getJsonString(0, "已取消关注");
    }

    @GetMapping(path = "/followee/{userId}")
    public String getFollowees(@PathVariable("userId") int userId, Page page, Model model) {
        User user = userService.findUserById(userId);

        if (user == null) {
            throw new RuntimeException("该用户不存在！");
        }
        model.addAttribute("user", user);

        page.setLimit(5);
        page.setPath("/followee/" + userId);
        page.setRows((int)followService.findFolloweeCount(userId, ENTITY_TYPE_USER));

        List<Map<String, Object>> userList = followService.findFollowees(userId, page.getOffset(), page.getLimit());
        if (userList != null) {
            for (Map<String, Object> map : userList) {
                // 关注人
                User u = (User)map.get("user");
                map.put("hasFollowed", hasFollowed(u.getId()));
            }
        }
        model.addAttribute("users", userList);
        return "/site/followee";
    }

    @GetMapping(path = "/follower/{userId}")
    public String getFollowers(@PathVariable("userId") int userId, Page page, Model model) {
        User user = userService.findUserById(userId);

        if (user == null) {
            throw new RuntimeException("该用户不存在！");
        }
        model.addAttribute("user", user);

        page.setLimit(5);
        page.setPath("/follower/" + userId);
        page.setRows((int)followService.findFollowerCount(ENTITY_TYPE_USER, userId));

        List<Map<String, Object>> userList = followService.findFollowers(userId, page.getOffset(), page.getLimit());
        if (userList != null) {
            for (Map<String, Object> map : userList) {
                User u = (User)map.get("user");
                map.put("hasFollowed", hasFollowed(u.getId()));
            }
        }
        model.addAttribute("users", userList);
        return "/site/follower";
    }

    private boolean hasFollowed(int userId) {
        if (hostHolder.getUser() == null) {
            return false;
        }
        return followService.hasFollowed(hostHolder.getUser().getId(), ENTITY_TYPE_USER, userId);
    }
}
