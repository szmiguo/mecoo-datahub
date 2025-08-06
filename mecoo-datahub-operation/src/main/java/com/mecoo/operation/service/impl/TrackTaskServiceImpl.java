package com.mecoo.operation.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mecoo.common.utils.DateUtils;
import com.mecoo.operation.domain.TrackTaskItem;
import com.mecoo.operation.enums.TrackTaskType;
import com.mecoo.operation.mapper.TrackTaskItemMapper;
import com.mecoo.operation.mapper.TrackTaskMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mecoo.operation.domain.TrackTask;
import com.mecoo.operation.service.ITrackTaskService;
import com.mecoo.common.core.text.Convert;

/**
 * 追踪任务Service业务层处理
 *
 * @author mecoo
 * @date 2025-07-08
 */
@Service
public class TrackTaskServiceImpl implements ITrackTaskService {

    @Autowired
    private TrackTaskMapper trackTaskMapper;

     @Autowired
    private TrackTaskItemMapper trackTaskItemMapper;


    @Override
    public List<TrackTask> selectTodoTrackTask(Date dateTime, TrackTaskType taskType) {
        //构造查询参数
        Map<String, Object> param = new HashMap<>();
        param.put("dateTime", dateTime);
        param.put("trackType", taskType.getValue());
        return trackTaskMapper.selectTodoTrackTask(param);
    }

    @Override
    public List<TrackTaskItem> selectTodoTrackTaskItem(Date dateTime, TrackTaskType taskType) {
        //构造查询参数
        Map<String, Object> param = new HashMap<>();
        param.put("dateTime", dateTime);
        param.put("trackType", taskType.getValue());
        return trackTaskItemMapper.selectTodoTrackTaskItem(param);
    }
}
