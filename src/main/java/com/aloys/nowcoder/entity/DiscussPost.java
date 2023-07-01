package com.aloys.nowcoder.entity;

import javax.xml.crypto.Data;

public class DiscussPost {
    private int id;
    private int userId;
    private String title;
    private String content;
    private int type;
    private int status;
    private Data createTime;
    private int commentCount;
    private double score;

    public DiscussPost() {
    }

    public DiscussPost(int id, int userId, String title, String content, int type, int status, Data createTime, int commentCount, double score) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.type = type;
        this.status = status;
        this.createTime = createTime;
        this.commentCount = commentCount;
        this.score = score;
    }

    /**
     * 获取
     * @return id
     */
    public int getId() {
        return id;
    }

    /**
     * 设置
     * @param id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * 获取
     * @return userId
     */
    public int getUserId() {
        return userId;
    }

    /**
     * 设置
     * @param userId
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }

    /**
     * 获取
     * @return title
     */
    public String getTitle() {
        return title;
    }

    /**
     * 设置
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 获取
     * @return content
     */
    public String getContent() {
        return content;
    }

    /**
     * 设置
     * @param content
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * 获取
     * @return type
     */
    public int getType() {
        return type;
    }

    /**
     * 设置
     * @param type
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     * 获取
     * @return status
     */
    public int getStatus() {
        return status;
    }

    /**
     * 设置
     * @param status
     */
    public void setStatus(int status) {
        this.status = status;
    }

    /**
     * 获取
     * @return createTime
     */
    public Data getCreateTime() {
        return createTime;
    }

    /**
     * 设置
     * @param createTime
     */
    public void setCreateTime(Data createTime) {
        this.createTime = createTime;
    }

    /**
     * 获取
     * @return commentCount
     */
    public int getCommentCount() {
        return commentCount;
    }

    /**
     * 设置
     * @param commentCount
     */
    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    /**
     * 获取
     * @return score
     */
    public double getScore() {
        return score;
    }

    /**
     * 设置
     * @param score
     */
    public void setScore(double score) {
        this.score = score;
    }

    public String toString() {
        return "DiscussionPost{id = " + id + ", userId = " + userId + ", title = " + title + ", content = " + content + ", type = " + type + ", status = " + status + ", createTime = " + createTime + ", commentCount = " + commentCount + ", score = " + score + "}";
    }
}
