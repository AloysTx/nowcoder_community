package com.aloys.nowcoder.controller.interceptor;

import com.aloys.nowcoder.entity.LoginTicket;
import com.aloys.nowcoder.entity.User;
import com.aloys.nowcoder.service.UserService;
import com.aloys.nowcoder.utils.CookieUtils;
import com.aloys.nowcoder.utils.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Autowired
    UserService userService;

    @Autowired
    HostHolder hostHolder;

    // 根据登录凭证得到用户对象 user
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 从 cookie 中获取登录平凭证 ticket
        String ticket = CookieUtils.getCookieValue(request, "ticket");
        // 非空表示处于登录状态
        if (ticket != null) {
            LoginTicket loginTicket = userService.findLoginTicket(ticket);
            // 非空 且状态为有效（0） 且失效时间未到
            if (loginTicket != null && loginTicket.getStatus() == 0 && loginTicket.getExpired().after(new Date())) {
                // 根据凭证得到用户 id ，根据 id 查询用户
                User user = userService.findUserById(loginTicket.getUserId());
                // 在本次请求中持有 user 对象（线程结束前）
                hostHolder.setUser(user);
            }
        }
        return true;
    }

    // 将 preHandle 查询到的 user 加入 Model，以便模板引擎使用
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();
        if (user != null && modelAndView != null) {
            modelAndView.addObject("loginUser", user);
        }
    }

    // 模板引擎已使用完 user，清理数据;
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        hostHolder.remove();
    }
}
