package com.aloys.nowcoder.controller;

import com.aloys.nowcoder.annotation.LoginRequired;
import com.aloys.nowcoder.entity.User;
import com.aloys.nowcoder.service.FollowService;
import com.aloys.nowcoder.service.LikeService;
import com.aloys.nowcoder.service.UserService;
import com.aloys.nowcoder.utils.CommonUtils;
import com.aloys.nowcoder.utils.HostHolder;
import com.aloys.nowcoder.utils.NowCoderConstants;
import org.apache.catalina.startup.HomesUserDatabase;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

@Controller
@RequestMapping("/user")
public class UserController implements NowCoderConstants {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Value("${nowcoder.path.domain}")
    private String domain;

    @Value("${nowcoder.path.upload}")
    private String uploadPath;

    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private FollowService followService;

    @Autowired
    private HostHolder hostHolder;

    @LoginRequired
    @GetMapping("/setting")
    public String getSettingPage() {
        return "/site/setting";
    }

    @LoginRequired
    @PostMapping("/upload")
    public String uploadHeader(MultipartFile header, Model model) {
        // 判断空值，为空说明用户未传入图片
        if(header == null) {
            model.addAttribute("error", "请选择要上传的图片！");
            return "/site/setting";
        }
        // 记录文件后缀
        String fileName = header.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        if(StringUtils.isBlank(suffix)) {
            model.addAttribute("error", "不正确的文件格式！");
            return "/site/setting";
        }

        // 生成随机文件前缀
        fileName = CommonUtils.generateUUID() + suffix;
        // 指定文件存放路径
        File dest = new File(uploadPath + "/" + fileName);
        try {
            header.transferTo(dest);
        } catch (IOException e) {
            logger.error("上传文件失败：" + e.getMessage());
            throw new RuntimeException("上传文件失败，服务器发生异常");
        }

        // 更新用户头像路径
        // 先获取当前线程对应用户
        User user = hostHolder.getUser();
        // 更新用户头像资源请求路径，就是映射到下面的 getHeader 方法了
        String headerUrl = domain + contextPath + "/user/header/" + fileName;;
        userService.updateHeader(user.getId(), headerUrl);
        // 这里重定向是为了刷新头像
        return "redirect:/user/setting";
    }

    @GetMapping("/header/{fileName}")
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response) {
        // 服务器存储路径
        fileName = uploadPath + "/" + fileName;
        // 获取后缀
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        // 响应图片
        response.setContentType("image/" + suffix);
        try (OutputStream os = response.getOutputStream();
             FileInputStream fis = new FileInputStream(fileName)) {
            byte[] buffer = new byte[1024];
            int b = 0;
            while((b = fis.read(buffer)) != -1) {
                os.write(buffer, 0, b);
            }

        } catch (IOException e) {
            logger.error("读取头像失败：" + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    // 个人主页
    @GetMapping(path = "/profile/{userId}")
    public String getProfilePage(@PathVariable("userId") int userId, Model model) {
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("该用户不存在！");
        }

        // 用户
        model.addAttribute("user", user);
        // 获赞数量
        int likeCount = likeService.findUserLikeCount(userId);
        model.addAttribute("likeCount", likeCount);

        // 关注数量
        long followeeCount = followService.findFolloweeCount(userId, ENTITY_TYPE_USER);
        model.addAttribute("followeeCount", followeeCount);

        // 粉丝数量
        long followerCount = followService.findFollowerCount(ENTITY_TYPE_USER, userId);
        model.addAttribute("followerCount", followerCount);

        // 是否已关注
        boolean hasFollowed = false;
        if (hostHolder.getUser() != null) {
            hasFollowed = followService.hasFollowed(hostHolder.getUser().getId(), ENTITY_TYPE_USER, userId);
        }
        model.addAttribute("hasFollowed", hasFollowed);
        return "/site/profile";
    }
}
