package com.aloys.nowcoder.utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class CookieUtils {
    public static String getCookieValue(HttpServletRequest request, String name) {
        // 空值处理
        if (request == null || name == null) {
            throw new IllegalArgumentException("参数为空");
        }

        // 遍历 cookies 搜索目标 cookie
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for(Cookie cookie: cookies) {
                if (cookie.getName().equals(name)) {
                    return cookie.getValue();
                }
            }
        }

        // 目标 cookie 不存在，返回 null
        return null;
    }
}
