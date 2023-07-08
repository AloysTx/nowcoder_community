package com.aloys.nowcoder.controller;

import com.aloys.nowcoder.entity.User;
import com.aloys.nowcoder.service.UserService;
import com.aloys.nowcoder.utils.NowCoderConstants;
import com.google.code.kaptcha.Producer;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

@Controller
public class LoginController implements NowCoderConstants {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Value("server.servlet.context-path")
    private String contextPath;

    @Autowired
    UserService userService;

    @Autowired
    Producer kaptchaProducer;

    // 注意，这里注解里的是浏览器访问的 url 地址
    // return 的是 templates 里的 html 文件路径
    @GetMapping("/register")
    public String getRegisterPage() {
        return "/site/register";
    }

    @GetMapping("/login")
    public String getLoginPage() {
        return "/site/login";
    }

    @PostMapping("/register")
    public String register(Model model, User user) {
        Map<String, Object> map = userService.register(user);
        if(map.isEmpty()) {
            // 注册成功，给出提示，跳转首页（因为还没激活，所以不跳转登录界面）
            // 提示
            model.addAttribute("msg", "注册成功，已向您的邮箱发送了一封激活邮件，请尽快激活！");
            // 跳转页面
            model.addAttribute("target", "/index");
            return "/site/operate-result";
        } else {
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            model.addAttribute("emailMsg", map.get("emailMsg"));
            return "/site/register";
        }
    }

    // http://localhost:8080/nowcoer/activation/userId/activationCode
    @GetMapping("/activation/{userId}/{code}")
    public String activation(Model model, @PathVariable("userId") int userId, @PathVariable("code") String code) {
        int result = userService.activation(userId, code);
        if(result == ACTIVATION_SUCCESS) {
            // 激活成功，提示并跳转登录界面
            model.addAttribute("msg", "激活成功，您可以正常使用您的账号了！");
            model.addAttribute("target", "/login");
            return "/site/operate-result";
        } else if (result == ACTIVATION_REPEAT) {
            // 重复激活，提示并跳转登录界面
            model.addAttribute("msg", "该账号已激活，请勿重复操作！");
            model.addAttribute("target", "/login");
            return "/site/operate-result";
        } else {
            // 激活失败，提示并跳转
            model.addAttribute("msg", "激活失败，您的激活码不正确！");
            model.addAttribute("target", "/index");
            return "/site/operate-result";
        }

    }

    @PostMapping("/login")
    public String login(Model model, String username, String password, String verificationCode,
                        boolean rememberMe, HttpServletResponse response, HttpSession session) {
        // 检查验证码
        if(StringUtils.isBlank(verificationCode)) {
            model.addAttribute("verificationCodeMsg", "验证码不能为空！");
            return "/site/login";
        }
        String kaptcha = (String) session.getAttribute("kaptcha");
        if(StringUtils.isBlank(kaptcha) || !verificationCode.equalsIgnoreCase(kaptcha)) {
            model.addAttribute("verificationCodeMsg", "验证码错误！");
            return "/site/login";
        }
        // 检查账号密码
        int duration = rememberMe ? REMEMBER_DURATION : DEFAULT_DURATION;
        Map<String, Object> map = userService.login(username, password, duration);
        if (map.containsKey("ticket")) {
            // 登录成功，将登录凭证的 Cookie 发送给页面
            Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
            cookie.setPath(contextPath);
            cookie.setMaxAge(duration);
            response.addCookie(cookie);
            // 重定向到主页
            return "redirect:/index";
        } else {
            // 登录失败，返回错误信息
            // 返回 usernameMsg，如果不是用户名出错，则该值为 null
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            // 同上
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            return "/site/login";
        }
    }

    @GetMapping("/logout")
    public String logout(@CookieValue("ticket") String ticket) {
        userService.logout(ticket);
        return "redirect:/login";
    }

    // 因为下次登录时要校验 验证码 是否正确，所以需要存储验证码，而验证码是敏感信息，所以用 Session
    @GetMapping("/kaptcha")
    public void getKaptcha(HttpServletResponse response, HttpSession session) {
        // 生成验证码
        String text = kaptchaProducer.createText();
        BufferedImage image = kaptchaProducer.createImage(text);

        // 将验证码 text 存入 session
        session.setAttribute("kaptcha", text);

        // image 输出给浏览器，因为 response 对象由 SpringMVC 维护，所以不必手动关闭流
        response.setContentType("image/png");
        try {
            OutputStream os = response.getOutputStream();
            ImageIO.write(image, "png", os);
        } catch (IOException e) {
            logger.error("响应验证失败：" + e.getMessage());
        }
    }

}
