package com.mecoo.spider.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mecoo.spider.util.MediaTypeUtil;
import lombok.Data;


import java.time.LocalDateTime;

/**
 * 存储单个Reel数据的类
 */
@Data
@TableName("dh_post_data")
public class PostData {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("social_media")
    private String socialMedia;

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

    @TableField("post_user_name")
    private String postUserName;

    @TableField("created_time")
    private LocalDateTime createTime;

    @TableField("updated_time")
    private LocalDateTime updateTime;

    public PostData() {
        this.createTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
    }

    public PostData(String shortCode, int playCount, int likeCount, int commentCount, String postId, int mediaType, String postUserId) {
        this();
        this.shortCode = shortCode;
        this.playCount = playCount;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.postId = postId;
        this.mediaType = mediaType;
        this.postUserId = postUserId;
    }

    public PostData(String shortCode, int playCount, int likeCount, int commentCount, String postId, int mediaType, String postUserId, String postUserName) {
        this();
        this.shortCode = shortCode;
        this.playCount = playCount;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.postId = postId;
        this.mediaType = mediaType;
        this.postUserId = postUserId;
        this.postUserName = postUserName;
    }


    @Override
    public String toString() {
        return String.format("ID: %s | PostID: %s | UserID: %s | UserName: %s | 类型: %s | 播放数: %d | 点赞数: %d | 评论数: %d",
                shortCode, postId, postUserId, postUserName, MediaTypeUtil.getMediaTypeDescription(mediaType), playCount, likeCount, commentCount);
    }
}
