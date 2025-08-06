package com.mecoo.web.controller.operation.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mecoo.common.annotation.Excel;
import com.mecoo.common.core.domain.BaseEntity;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * KOL发布的社媒视频信息对象 kol_sm_video
 *
 * @author mecoo
 * @date 2025-06-26
 */
@Data
public class KolPostDTO extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 自增id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * post_id
     */
    @JsonProperty("post_id")
    @Excel(name = "post_id")
    private String postId;

    /**
     * 内容URL
     */
    @JsonProperty("url")
    private String postUrl;

    /**
     * 发布的社媒平台
     */
    @Excel(name = "发布的社媒平台")
    private String postSm;


    @JsonProperty("content_type")
    private String mediaType;

    /**
     * KOL ID
     */
    @Excel(name = "KOL ID")
    @JsonProperty("user_posted_id")
    private String postUserId;

    /**
     * 发布者全名
     */
    @JsonProperty("user_posted")
    @Excel(name = "发布者全名")
    private String postUser;

    /**
     * 内容发布时间
     */
    @JsonProperty("date_posted")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    @Excel(name = "内容发布时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date postDate;

    /**
     * 内容短码
     */
    @JsonProperty("shortcode")
    @Excel(name = "内容短码")
    private String postShortcode;

    /**
     * 内容描述
     */
    @JsonProperty("description")
    @Excel(name = "内容描述")
    private String description;


    /**
     * 标签
     */
    @JsonProperty("hashtags")
    private List<String> hashTags;

    /**
     * 播放次数
     */
    @JsonProperty("video_play_count")
    @Excel(name = "播放次数")
    private Long playCount;

    /**
     * 完播次数
     */
    @JsonProperty("video_view_count")
    @Excel(name = "完播次数")
    private Long viewCount;

    /**
     * 点赞次数
     */
    @JsonProperty("likes")
    @Excel(name = "点赞次数")
    private Long likeCount;

    /**
     * 评论次数
     */
    @JsonProperty("num_comments")
    @Excel(name = "评论次数")
    private Long commentCount;


    /**
     * 被关注数
     */
    @JsonProperty("followers")
    @Excel(name = "被关注数")
    private Long followerCount;


    /**
     * 内容ID
     */
    @JsonProperty("content_id")
    @Excel(name = "内容ID")
    private String contentId;

    /**
     * $column.columnComment
     */
    @JsonProperty("product_type")
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String productType;

    /**
     * 视频长度
     */
    @JsonProperty("length")
    @Excel(name = "视频长度")
    private String length;

    /**
     * is_paid_partnership
     */
    @JsonProperty("is_paid_partnership")
    @Excel(name = "is_paid_partnership")
    private String isPaidPartnership;

    public String getMediaType() {
        if (mediaType == null) {
            return null;
        }
        return mediaType.toLowerCase();
    }
}