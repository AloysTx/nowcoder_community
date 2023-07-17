package com.aloys.nowcoder.service;

import com.aloys.nowcoder.entity.User;
import com.aloys.nowcoder.utils.NowCoderConstants;
import com.aloys.nowcoder.utils.RedisKeyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FollowService implements NowCoderConstants {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private UserService userService;

    // 关注
    public void follow(int userId, int entityType, int entityId) {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String followerKey = RedisKeyUtils.getFollowerKey(entityType, entityId);
                String followeeKey = RedisKeyUtils.getFolloweeKey(userId, entityType);

                // 启动事务
                operations.multi();

                operations.opsForZSet().add(followeeKey, entityId, System.currentTimeMillis());
                operations.opsForZSet().add(followerKey, userId, System.currentTimeMillis());

                return redisTemplate.exec();
            }
        });
    }

    // 取消关注
    public void unfollow(int userId, int entityType, int entityId) {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String followerKey = RedisKeyUtils.getFollowerKey(entityType, entityId);
                String followeeKey = RedisKeyUtils.getFolloweeKey(userId, entityType);

                operations.multi();

                // 和关注类似，只是变成 remove 操作
                operations.opsForZSet().remove(followeeKey, entityId);
                operations.opsForZSet().remove(followerKey, userId);

                return redisTemplate.exec();
            }
        });
    }

    // 查询关注的实体的关注数量
    public long findFolloweeCount(int userId, int entityType) {
        String followeeKey = RedisKeyUtils.getFolloweeKey(userId, entityType);
        return redisTemplate.opsForZSet().zCard(followeeKey);
    }

    // 查询实体的粉丝数量
    public long findFollowerCount(int entityType, int entityId) {
        String followerKey = RedisKeyUtils.getFollowerKey(entityType, entityId);
        return redisTemplate.opsForZSet().zCard(followerKey);
    }

    // 查询当前用户是否关注该实体
    public boolean hasFollowed(int userId, int entityType, int entityId) {
        String followeeKey = RedisKeyUtils.getFolloweeKey(userId, entityType);
        return redisTemplate.opsForZSet().score(followeeKey, entityId) != null;
    }

    // 查询某个用户关注的人
    public List<Map<String, Object>> findFollowees(int userId, int offset, int limit) {
        String followeeKey = RedisKeyUtils.getFolloweeKey(userId, ENTITY_TYPE_USER);
        Set<Object> targetIds = redisTemplate.opsForZSet().reverseRange(followeeKey, offset, offset + limit - 1);

        if (targetIds == null) {
            return null;
        }

        List<Map<String, Object>> list = new ArrayList<>();
        for (Object targetId : targetIds) {
            Map<String, Object> map = new HashMap<>();
            User user = userService.findUserById((Integer) targetId);
            map.put("user", user);
            Double score = redisTemplate.opsForZSet().score(followeeKey, targetId);
            map.put("followTime", new Date(score.longValue()));
            list.add(map);
        }
        return list;
    }

    // 查询某个用户的粉丝
    public List<Map<String, Object>> findFollowers(int userId, int offset, int limit) {
        String followerKey = RedisKeyUtils.getFollowerKey(ENTITY_TYPE_USER, userId);
        Set<Object> targetIds = redisTemplate.opsForZSet().reverseRange(followerKey, offset, offset + limit - 1);

        if (targetIds == null) {
            return null;
        }

        List<Map<String, Object>> list = new ArrayList<>();
        for (Object targetId : targetIds) {
            Map<String, Object> map = new HashMap<>();
            User user = userService.findUserById((Integer) targetId);
            map.put("user", user);
            Double score = redisTemplate.opsForZSet().score(followerKey, targetId);
            map.put("followTime", new Date(score.longValue()));
            list.add(map);
        }
        return list;
    }
}
