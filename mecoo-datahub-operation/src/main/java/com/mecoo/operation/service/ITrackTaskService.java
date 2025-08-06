package com.mecoo.operation.service;

import java.util.Date;
import java.util.List;

import com.mecoo.operation.domain.TrackTask;
import com.mecoo.operation.domain.TrackTaskItem;
import com.mecoo.operation.enums.TrackTaskType;

/**
 * 追踪任务Service接口
 *
 * @author mecoo
 * @date 2025-07-08
 */
public interface ITrackTaskService {

    /**
     * 查询满足如下条件，需要追踪的任务：
     * 1.传入的 dateTime 处于任务的开始时间和结束时间内
     * 2.任务的类型属于传入的 taskType
     * @param dateTime 时间
     * @param taskType 任务类型
     * @return 追踪任务
     */
    List<TrackTask> selectTodoTrackTask(Date dateTime, TrackTaskType taskType);



    /**
     * 查询满足如下条件，需要追踪的任务：
     * 1.传入的 dateTime 处于任务的开始时间和结束时间内
     * 2.任务的类型属于传入的 taskType
     * @param dateTime 时间
     * @param taskType 任务类型
     * @return 追踪任务
     */
    List<TrackTaskItem> selectTodoTrackTaskItem(Date dateTime, TrackTaskType taskType);

}
