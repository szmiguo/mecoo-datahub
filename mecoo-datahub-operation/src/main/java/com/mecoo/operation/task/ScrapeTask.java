package com.mecoo.operation.task;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.mecoo.operation.domain.TrackTaskItem;
import com.mecoo.operation.enums.TrackTaskType;
import com.mecoo.operation.scrapers.BrightDataInstagramScraper;
import com.mecoo.operation.service.ITrackTaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.lang.model.SourceVersion;
import java.util.*;

/**
 * @author: lin
 * @date: 2025-06-26 16:01
 */
@Slf4j
@Component("ScrapeTask")
public class ScrapeTask {

    @Autowired
    public ITrackTaskService trackTaskService;


    /**
     * 抓取社交平台帖子，该任务每天执行一次
     */
    public void scrapeSocialMediaPost() {

        List<TrackTaskItem> trackTaskItems = trackTaskService.selectTodoTrackTaskItem(new Date(), TrackTaskType.POST);
        //如果没有待执行任务，则直接返回
        if (CollUtil.isEmpty(trackTaskItems)) {
            return;
        }

        //对trackTaskItems 根据URL去重，避免重复抓取
        Set<String> urlSet = new HashSet<>();
        List<TrackTaskItem> distinctItems = trackTaskItems.stream()
                .filter(item -> urlSet.add(item.getTrackUrl()))  // 如果 name 已存在，返回 false
                .toList();

        List<String> trackUrls = new ArrayList<>(urlSet);
        String insReelsSracpeTask = BrightDataInstagramScraper.createIgPostSracpeTask(trackUrls);


        System.out.println("执行社媒抓取：" + insReelsSracpeTask + "\n" + JSON.toJSONString(distinctItems));
        System.out.println("insReelsSracpeTask = " + insReelsSracpeTask);
    }


    //
//    public void ryMultipleParams(String s, Boolean b, Long l, Double d, Integer i) {
//        System.out.println(StringUtils.format("执行多参方法： 字符串类型{}，布尔类型{}，长整型{}，浮点型{}，整形{}", s, b, l, d, i));
//    }
//
//    public void ryParams(String params) {
//        System.out.println("执行有参方法：" + params);
//    }

}
