package com.aloys.nowcoder.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RedisConfigTest {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    public void testRedisString() {
        String key = "testKey";
        redisTemplate.opsForValue().set(key, 2);
        System.out.println(redisTemplate.opsForValue().get(key));
        redisTemplate.opsForValue().increment(key);
        System.out.println(redisTemplate.opsForValue().get(key));
    }

    @Test
    public void testRedisSet() {
        String key = "setKey";
        redisTemplate.opsForSet().add(key, "aaa", "bbb", "ccc");
        System.out.println(redisTemplate.opsForSet().size("bac"));
    }

    @Test
    public void testRedisZset() {
        String key = "zSetKey";
        redisTemplate.opsForZSet().add(key, "张三", 50);
        redisTemplate.opsForZSet().add(key, "李四", 40);
        redisTemplate.opsForZSet().add(key, "王五", 80);
        redisTemplate.opsForZSet().add(key, "陈7", 90);
        System.out.println(redisTemplate.opsForZSet().zCard(key));
        System.out.println(redisTemplate.opsForZSet().score(key, "李四"));
        System.out.println(redisTemplate.opsForZSet().rank(key, "陈7"));
//        System.out.println(redisTemplate.opsForZSet().reverseRank(key, "陈7"));
    }
}