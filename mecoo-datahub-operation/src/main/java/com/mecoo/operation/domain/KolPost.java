package com.mecoo.operation.domain;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mecoo.common.annotation.Excel;
import com.mecoo.common.core.domain.BaseDO;
import com.mecoo.operation.enums.SocialMediaPlatform;
import com.mecoo.operation.enums.SocialMediaType;
import lombok.Data;

import java.util.Date;

/**
 * KOL发布的社媒视频信息对象 op_kol_post
 *
 * @author mecoo
 * @date 2025-06-26
 */
@Data
@TableName("op_kol_post")
public class KolPost extends BaseDO {
    private static final long serialVersionUID = 1L;

    /**
     * post_id
     */
    private String postId;


    /**
     * 内容URL
     */
    private String postUrl;

    /**
     * 发布的社媒平台
     */
    @EnumValue
    private SocialMediaPlatform postSm;


    /**
     * 发布的媒体类型
     */
//    @EnumValue
    private String mediaType;


    /**
     * KOL ID
     */
    private String postUserId;

    /**
     * 发布者全名
     */
    private String postUser;

    /**
     * 内容发布时间
     */
    @Excel(name = "内容发布时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date postDate;

    /**
     * 内容短码
     */
    private String postShortcode;

    /**
     * 内容描述
     */
    private String description;

    /**
     * 播放次数
     */
    private Long playCount;

    /**
     * 完播次数
     */
    private Long viewCount;

    /**
     * 点赞次数
     */
    private Long likeCount;

    /**
     * 评论次数
     */
    private Long commentCount;

    /**
     * 被关注数
     */
    private Long followerCount;

    /**
     * 内容ID
     */
    private String contentId;

    /**
     * $column.columnComment
     */
    private String productType;

    /**
     * 视频长度
     */
    @Excel(name = "视频长度")
    private String length;

    /**
     * is_paid_partnership
     */
    private String isPaidPartnership;


}