package com.aloys.nowcoder.utils;

public class RedisKeyUtils {

    private static final String SPILT = ":"; // Redis 的 key 中间常用 : 分隔
    private static final String PREFIX_ENTITY_LIKE = "like:entity";
    private static final String PREFIX_USER_LIKE = "like:user";
    private static final String PREFIX_FOLLOWER = "follower";
    private static final String PREFIX_FOLLOWEE = "followee";
    private static final String PREFIX_KAPTCHA = "kaptcha";
    private static final String PREFIX_TICKET = "ticket";
    private static final String PREFIX_USER = "user";
    private static final String PREFIX_UV = "uv";
    private static final String PREFIX_DAU = "dau";
    private static final String PREFIX_POST = "post";

    // 某个实体的赞，（帖子/评论）
    // like:entity:entityType:entityId -> set(userId)
    // 这里用 set 而不是单纯的 int 记录点赞数是因为以后可能还需要获取点赞人是谁，而点赞数直接 set 得到总数就可以了
    public static String getEntityLikeKey(int entityType, int entityId) {
        return PREFIX_ENTITY_LIKE + SPILT + entityType + SPILT + entityId;
    }

    // 某个用户的赞
    // like:user:userId -> int
    public static String getUserLikeKey(int userId) {
        return PREFIX_USER_LIKE + SPILT + userId;
    }

    // 某个用户关注的实体
    // followee:userId:entityType -> zset(entityId, now)（以关注时间排序）
    public static String getFolloweeKey(int userId, int entityType) {
        return PREFIX_FOLLOWEE + SPILT + userId + SPILT +entityType;
    }

    // 某个实体拥有的粉丝
    // follower：entityType:entityId -> zset(UserId, now)（以关注时间排序）
    public static String getFollowerKey(int entityType, int entityId) {
        return PREFIX_FOLLOWER + SPILT + entityType + SPILT + entityId;
    }

    // 登录验证码
    public static String getKaptchaKey(String owner) {
        return PREFIX_KAPTCHA + SPILT + owner;
    }

    // 登录的凭证
    public static String getTicketKey(String ticket) {
        return PREFIX_TICKET + SPILT + ticket;
    }

    // 用户
    public static String getUserKey(int userId) {
        return PREFIX_USER + SPILT + userId;
    }
}
