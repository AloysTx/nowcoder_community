package com.aloys.nowcoder.utils;

import com.aloys.nowcoder.entity.User;
import org.springframework.stereotype.Component;

/**
 * 持有用户信息，用来代替 session
 */
@Component
public class HostHolder {
    // 浏览器访问服务器是多对一的关系，也就是多线程环境，
    // 使用 ThreadLocal 进行线程隔离
    public ThreadLocal<User> users = new ThreadLocal<>();

    // 设置当前线程 user
    public void setUser(User user) {
        users.set(user);
    }

    // 获取当前线程 user
    public User getUser() {
        return users.get();
    }

    // 移除当前线程 user
    public void remove() {
        users.remove();
    }
}
