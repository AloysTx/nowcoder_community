package com.aloys.nowcoder.service;

import com.aloys.nowcoder.dao.LoginTicketMapper;
import com.aloys.nowcoder.dao.UserMapper;
import com.aloys.nowcoder.entity.LoginTicket;
import com.aloys.nowcoder.entity.Page;
import com.aloys.nowcoder.entity.User;
import com.aloys.nowcoder.utils.CommonUtils;
import com.aloys.nowcoder.utils.NowCoderConstants;
import com.aloys.nowcoder.utils.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.*;

@Service
public class UserService implements NowCoderConstants {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${nowcoder.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    public User findUserById(int id) {
        return userMapper.selectById(id);
    }

    // 通过 ticket 查询 login_ticket 记J录
    public LoginTicket findLoginTicket(String ticket) {
        return loginTicketMapper.selectByTicket(ticket);
    }

    public Map<String, Object> register(User user) {
        Map<String, Object> map = new HashMap<>();
        if(user == null) {
            throw new IllegalArgumentException("参数不能为空");
        }
        // 账号为空
        if(StringUtils.isBlank(user.getUsername())) {
            map.put("usernameMsg", "账号不能为空");
            return map;
        }
        // 密码为空
        if(StringUtils.isBlank(user.getPassword())) {
            map.put("passwordMsg", "密码不能为空");
            return map;
        }
        // 邮箱为空
        if(StringUtils.isBlank(user.getEmail())) {
            map.put("emailMsg", "邮箱不能为空");
            return map;
        }

        // 验证账号是否重复
        User selected = userMapper.selectByName(user.getUsername());
        if(selected != null) {
            map.put("usernameMsg", "该用户名已存在");
            return map;
        }
        // 验证邮箱是否重复
        selected = userMapper.selectByEmail(user.getEmail());
        if(selected != null) {
            map.put("emailMsg", "该邮箱已被注册");
            return map;
        }

        // 注册用户（实质就是加入数据库）
        // 生成随机 salt，取5位
        user.setSalt(CommonUtils.generateUUID().substring(0, 5));
        // 加密后的密码覆盖原密码
        user.setPassword(CommonUtils.md5(user.getPassword() + user.getSalt()));
        // 用户类型，普通
        user.setType(0);
        // 用户状态，未激活
        user.setStatus(0);
        // 生成激活码
        user.setActivationCode(CommonUtils.generateUUID());
        // 赋随机初始头像，牛客网有 1001 个初始头像，所以随机数 0~1000
        user.setHeaderUrl(String.format("https://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        // 注册时间
        user.setCreateTime(new Date());
        // 添加到库
        userMapper.insertUser(user);

        // 发送激活邮件
        Context context = new Context();
        context.setVariable("email", user.getEmail());
        // 生成激活链接，示例 http://localhost:8080/nowcoer/activation/userId/activationCode
        String url = domain + contextPath + "/activation/" + user.getId() + "/" + user.getActivationCode();
        context.setVariable("url", url);
        String content = templateEngine.process("mail/activation", context);
        mailClient.sendMail(user.getEmail(), "牛客账号激活", content);
        // 完成激活，返回空 map
        return map;
    }

    // 激活账号
    public int activation(int userId, String code) {
        User user = userMapper.selectById(userId);
        if (user.getStatus() == 1) {
            return ACTIVATION_REPEAT;
        } else if (user.getActivationCode().equals(code)) {
            userMapper.updateStatus(userId, 1);
            return ACTIVATION_SUCCESS;
        } else {
            return ACTIVATION_FAILURE;
        }
    }

    // 用户登录
    // duration 表示登录凭证的有效持续时间（单位：秒），超过该世间就会 expire
    public Map<String, Object> login(String username, String password, int duration) {
        Map<String, Object> map = new HashMap<>();
        // 空值处理
        if (StringUtils.isBlank(username)) {
            map.put("usernameMsg", "账号不能为空！");
            return map;
        } else if (StringUtils.isBlank(password)) {
            map.put("passwordMsg", "密码不能为空！");
            return map;
        }

        // 验证账号
        User user = userMapper.selectByName(username);
        // 验证账号是否存在
        if (user == null) {
            map.put("usernameMsg", "该账号不存在！");
            return map;
        }
        // 验证账号是否激活
        if (user.getStatus() == 0) {
            map.put("usernameMsg", "该账号未激活！");
            return map;
        }

        // 验证密码
        password = CommonUtils.md5(password + user.getSalt());
        if (!Objects.equals(user.getPassword(), password)) {
            map.put("passwordMsg", "密码不正确！");
            return map;
        }

        // 生成登录凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(CommonUtils.generateUUID());
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + duration * 1000));
        loginTicketMapper.insertLoginTicket(loginTicket);

        // 登录成功，返回登录凭证给浏览器
        map.put("ticket", loginTicket.getTicket());
        return map;
    }

    // 退出时需要 ticket 登录凭证来关联是哪个用户要退出
    public void logout(String ticket) {
        // 把 ticket 对应用户登录状态设为 1 即可
        loginTicketMapper.updateStatus(ticket, 1);
    }
}
