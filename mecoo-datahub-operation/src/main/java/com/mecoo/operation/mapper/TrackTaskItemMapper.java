package com.mecoo.operation.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mecoo.operation.domain.TrackTask;
import com.mecoo.operation.domain.TrackTaskItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 追踪任务详情Mapper接口
 *
 * @author mecoo
 * @date 2025-07-08
 */
@Mapper
public interface TrackTaskItemMapper extends BaseMapper<TrackTaskItem> {

        @Select("SELECT * FROM op_track_task_item WHERE track_type = #{trackType} AND start_time <= #{dateTime} AND end_time >= #{dateTime}")
        List<TrackTaskItem> selectTodoTrackTaskItem(Map<String, Object> param);

}
