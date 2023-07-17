package com.aloys.nowcoder.service;

import com.aloys.nowcoder.utils.RedisKeyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

@Service
public class LikeService {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // 点赞； 参数分别为 点赞（这个动作）的用户，点赞的目标实体类型，点赞的目标实体 id
    public void like(int userId, int entityType, int entityId, int entityUserId) {
//        String entityLikeKey = RedisKeyUtils.getEntityLikeKey(entityType, entityId);
//
//        boolean isMember = Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(entityLikeKey, userId));
//        if (isMember) {
//            // 如果已点赞，再点一次就是取消点赞
//            redisTemplate.opsForSet().remove(entityLikeKey, userId);
//        } else {
//            // 没点赞，点一次则点赞
//            redisTemplate.opsForSet().add(entityLikeKey, userId);
//        }
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String entityLikeKey = RedisKeyUtils.getEntityLikeKey(entityType, entityId);
                String userLikeKey = RedisKeyUtils.getUserLikeKey(entityUserId);
                // 查询放在事务外
                boolean isMember = Boolean.TRUE.equals(operations.opsForSet().isMember(entityLikeKey, userId));
                operations.multi();
                if (isMember) {
                    // 如果已点赞，再点一次就是取消点赞
                    operations.opsForSet().remove(entityLikeKey, userId);
                    operations.opsForValue().decrement(userLikeKey);
                } else {
                    // 没点赞，点一次则点赞
                    operations.opsForSet().add(entityLikeKey, userId);
                    operations.opsForValue().increment(userLikeKey);
                }
                return operations.exec();
            }
        });

    }

    // 查询某个实体得到的赞的数量
    public long findEntityLikeCount(int entityType, int entityId) {
        String entityLikeKey = RedisKeyUtils.getEntityLikeKey(entityType, entityId);
        return redisTemplate.opsForSet().size(entityLikeKey);
    }

    // 查询某人是否点赞了某实体
    public int findEntityLikeStatus(int userId, int entityType, int entityId) {
        String entityLikeKey = RedisKeyUtils.getEntityLikeKey(entityType, entityId);
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(entityLikeKey, userId)) ? 1 : 0;
    }

    // 查询某个用户获得的赞
    public int findUserLikeCount(int userId) {
        String userLikeKey = RedisKeyUtils.getUserLikeKey(userId);
        Integer count = (Integer) redisTemplate.opsForValue().get(userLikeKey);
        return count == null ? 0 : count;
    }
}
