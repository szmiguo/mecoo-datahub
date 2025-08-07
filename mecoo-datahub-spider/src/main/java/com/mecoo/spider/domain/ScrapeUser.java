package com.mecoo.spider.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 待抓取用户信息实体类
 */
@Data
@TableName("dh_scrape_user")
public class ScrapeUser {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("social_media")
    private String socialMedia;

    @TableField("user_name")
    private String userName;

    @TableField("start_time")
    private LocalDateTime startTime;

    @TableField("end_time")
    private LocalDateTime endTime;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;

    public ScrapeUser() {
        this.createTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
    }

    public ScrapeUser(String socialMedia, String userName, LocalDateTime startTime, LocalDateTime endTime) {
        this();
        this.socialMedia = socialMedia;
        this.userName = userName;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return String.format("ScrapeUser{id=%d, socialMedia='%s', userName='%s', startTime=%s, endTime=%s}",
                id, socialMedia, userName, startTime, endTime);
    }
}