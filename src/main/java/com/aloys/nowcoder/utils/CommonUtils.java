package com.aloys.nowcoder.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.Map;
import java.util.UUID;

import com.alibaba.fastjson2.JSONObject;

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

    // 封装数据为 JSON 格式，包括 代码 code，信息 msg 和对象
    public static String getJsonString(int code, String msg, Map<String, Object> map) {
        JSONObject json = new JSONObject();
        json.put("code", code);
        json.put("msg", msg);
        if (map != null) {
            for (String key : map.keySet()) {
                json.put(key, map.get(key));
            }
        }
        return json.toJSONString();
    }

    // 重载，提供不同签名方法
    public static String getJsonString(int code, String msg) {
        return getJsonString(code, msg, null);
    }

    public static String getJsonString(int code) {
        return getJsonString(code, null, null);
    }
}
