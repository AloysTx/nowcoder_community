package com.aloys.nowcoder.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.UUID;

public class CommonUtils {

    // 生成随机字符串 （通用唯一标识符）
    public static String generateUUID() {
        // 生成的 UUID 包含 dash '-'，去掉
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    // MD5 加密（密码 + salt（随机字符串）之后进行 MD5 加密，提高安全性）
    public static String md5(String key) {
        // 空串处理
        if(StringUtils.isBlank(key)) {
            return null;
        }
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }
}
