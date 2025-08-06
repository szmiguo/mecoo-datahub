package com.mecoo.spider.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mecoo.spider.util.MediaTypeUtil;


import java.time.LocalDateTime;

/**
 * 存储单个Reel数据的类
 */
@TableName("reel_data")
public class ReelData {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    @TableField("short_code")
    private String shortCode;
    
    @TableField("play_count")
    private int playCount;
    
    @TableField("like_count")
    private int likeCount;
    
    @TableField("comment_count")
    private int commentCount;
    
    @TableField("post_id")
    private String postId;
    
    @TableField("media_type")
    private int mediaType;
    
    @TableField("post_user_id")
    private String postUserId;
    
    @TableField("created_time")
    private LocalDateTime createdTime;
    
    @TableField("updated_time")
    private LocalDateTime updatedTime;

    public ReelData() {
        this.createdTime = LocalDateTime.now();
        this.updatedTime = LocalDateTime.now();
    }

    public ReelData(String shortCode, int playCount, int likeCount, int commentCount, String postId, int mediaType, String postUserId) {
        this();
        this.shortCode = shortCode;
        this.playCount = playCount;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.postId = postId;
        this.mediaType = mediaType;
        this.postUserId = postUserId;
    }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        
        public String getShortCode() { return shortCode; }
        public void setShortCode(String shortCode) { this.shortCode = shortCode; }
        
        public int getPlayCount() { return playCount; }
        public void setPlayCount(int playCount) { this.playCount = playCount; }
        
        public int getLikeCount() { return likeCount; }
        public void setLikeCount(int likeCount) { this.likeCount = likeCount; }
        
        public int getCommentCount() { return commentCount; }
        public void setCommentCount(int commentCount) { this.commentCount = commentCount; }
        
        public String getPostId() { return postId; }
        public void setPostId(String postId) { this.postId = postId; }
        
        public int getMediaType() { return mediaType; }
        public void setMediaType(int mediaType) { this.mediaType = mediaType; }
        
        public String getPostUserId() { return postUserId; }
        public void setPostUserId(String postUserId) { this.postUserId = postUserId; }
        
        public LocalDateTime getCreatedTime() { return createdTime; }
        public void setCreatedTime(LocalDateTime createdTime) { this.createdTime = createdTime; }
        
        public LocalDateTime getUpdatedTime() { return updatedTime; }
        public void setUpdatedTime(LocalDateTime updatedTime) { this.updatedTime = updatedTime; }

        @Override
        public String toString() {
            return String.format("ID: %s | PostID: %s | UserID: %s | 类型: %s | 播放数: %d | 点赞数: %d | 评论数: %d",
                shortCode, postId, postUserId, MediaTypeUtil.getMediaTypeDescription(mediaType), playCount, likeCount, commentCount);
        }
}
