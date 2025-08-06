package com.mecoo.operation.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mecoo.common.annotation.Excel;
import com.mecoo.common.core.domain.BaseDO;
import lombok.Data;

/**
 * KOL发布的社媒视频播放记录对象 kol_sm_video_record
 *
 * @author mecoo
 * @date 2025-06-26
 */
@Data
@TableName("op_kol_post_recod")
public class KolPostRecord extends BaseDO {
    private static final long serialVersionUID = 1L;

    /**
     * post_id
     */
    private String postId;

    /**
     * 内容短码
     */
    private String postShortcode;

    /**
     * KOL ID
     */
    private String postUserId;

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


}
