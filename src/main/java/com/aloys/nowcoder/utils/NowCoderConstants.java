package com.aloys.nowcoder.utils;

public interface NowCoderConstants {

    /**
     * 激活成功
     */
    int ACTIVATION_SUCCESS = 0;

    /**
     * 重复激活
     */
    int ACTIVATION_REPEAT = 1;

    /**
     * 激活失败
     */
    int ACTIVATION_FAILURE = 2;

    /**
     * 默认状态下登录凭证的有效时间：12 hours
     */
    int DEFAULT_DURATION = 3600 * 12;

    /**
     * 记住我 状态下登陆凭证的有效时间：100 days
     */
    int REMEMBER_DURATION = 3600 * 24 * 100;

    /**
     * 实体类型：帖子
     */
    int ENTITY_TYPE_POST = 1;

    /**
     * 实体类型：评论
     */
    int ENTITY_TYPE_COMMENT = 2;

}
