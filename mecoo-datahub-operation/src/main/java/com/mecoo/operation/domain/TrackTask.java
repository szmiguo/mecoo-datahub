package com.mecoo.operation.domain;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mecoo.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.mecoo.common.annotation.Excel;

/**
 * 追踪任务对象 op_track_task
 *
 * @author mecoo
 * @date 2025-07-08
 */
@Data
@NoArgsConstructor
public class TrackTask extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * $column.columnComment
     */
    private String id;

    /**
     * 任务名称
     */
    @Excel(name = "任务名称")
    private String taskName;

    /**
     * 追踪任务类型
     */
    @Excel(name = "追踪任务类型")
    private String trackType;

    /**
     * 任务开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Excel(name = "任务开始时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date startTime;

    /**
     * 任务结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Excel(name = "任务结束时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date endTime;


}
